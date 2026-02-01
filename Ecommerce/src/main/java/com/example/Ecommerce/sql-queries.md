# E-commerce SQL interview queries (with solutions)

> **Assumed schema (typical e-commerce):**
> - `users(id, name, email, created_at)`
> - `products(id, name, category_id, price, is_active)`
> - `categories(id, name)`
> - `orders(id, user_id, status, order_date, total_amount)`
> - `order_items(id, order_id, product_id, quantity, unit_price)`
> - `payments(id, order_id, status, paid_at, amount)`
> - `reviews(id, product_id, user_id, rating, created_at)`
> - `inventory(product_id, on_hand)`

## Foundational queries

1) **List active products with category name and price (sorted by price).**
```sql
SELECT p.id,
       p.name AS product_name,
       c.name AS category_name,
       p.price
FROM products AS p
JOIN categories AS c ON c.id = p.category_id
WHERE p.is_active = TRUE
ORDER BY p.price ASC;
```

2) **Find the top 10 most recent orders with customer email.**
```sql
SELECT o.id,
       o.order_date,
       o.status,
       o.total_amount,
       u.email
FROM orders AS o
JOIN users AS u ON u.id = o.user_id
ORDER BY o.order_date DESC
LIMIT 10;
```

3) **Total revenue by month for completed orders.**
```sql
SELECT DATE_TRUNC('month', o.order_date) AS month,
       SUM(o.total_amount) AS revenue
FROM orders AS o
WHERE o.status = 'COMPLETED'
GROUP BY DATE_TRUNC('month', o.order_date)
ORDER BY month;
```

4) **Average rating per product (include products with no reviews).**
```sql
SELECT p.id,
       p.name,
       AVG(r.rating) AS avg_rating
FROM products AS p
LEFT JOIN reviews AS r ON r.product_id = p.id
GROUP BY p.id, p.name
ORDER BY avg_rating DESC NULLS LAST;
```

5) **Find customers who have never placed an order.**
```sql
SELECT u.id,
       u.name,
       u.email
FROM users AS u
LEFT JOIN orders AS o ON o.user_id = u.id
WHERE o.id IS NULL;
```

6) **Compute order totals from order_items (validate stored totals).**
```sql
SELECT o.id AS order_id,
       o.total_amount AS stored_total,
       SUM(oi.quantity * oi.unit_price) AS computed_total,
       (o.total_amount - SUM(oi.quantity * oi.unit_price)) AS delta
FROM orders AS o
JOIN order_items AS oi ON oi.order_id = o.id
GROUP BY o.id, o.total_amount
ORDER BY ABS(o.total_amount - SUM(oi.quantity * oi.unit_price)) DESC;
```

## Advanced / harder queries

1) **Top 3 products by revenue within each category (window function).**
```sql
WITH product_revenue AS (
  SELECT p.id,
         p.name,
         p.category_id,
         SUM(oi.quantity * oi.unit_price) AS revenue
  FROM products AS p
  JOIN order_items AS oi ON oi.product_id = p.id
  JOIN orders AS o ON o.id = oi.order_id
  WHERE o.status = 'COMPLETED'
  GROUP BY p.id, p.name, p.category_id
)
SELECT pr.id,
       pr.name,
       c.name AS category_name,
       pr.revenue
FROM (
  SELECT pr.*,
         ROW_NUMBER() OVER (PARTITION BY pr.category_id ORDER BY pr.revenue DESC) AS rn
  FROM product_revenue AS pr
) AS pr
JOIN categories AS c ON c.id = pr.category_id
WHERE pr.rn <= 3
ORDER BY c.name, pr.revenue DESC;
```

2) **Customers with ≥3 orders in consecutive months (gaps-and-islands).**
```sql
WITH customer_months AS (
  SELECT o.user_id,
         DATE_TRUNC('month', o.order_date) AS month
  FROM orders AS o
  WHERE o.status = 'COMPLETED'
  GROUP BY o.user_id, DATE_TRUNC('month', o.order_date)
),
sequenced AS (
  SELECT cm.*,
         (DATE_PART('year', cm.month) * 12 + DATE_PART('month', cm.month))
         - ROW_NUMBER() OVER (PARTITION BY cm.user_id ORDER BY cm.month) AS grp
  FROM customer_months AS cm
)
SELECT user_id,
       MIN(month) AS streak_start,
       MAX(month) AS streak_end,
       COUNT(*) AS months_in_row
FROM sequenced
GROUP BY user_id, grp
HAVING COUNT(*) >= 3
ORDER BY months_in_row DESC, user_id;
```

3) **Repeat-purchase rate by product (buyers who purchased ≥2 times / total buyers).**
```sql
WITH product_buyers AS (
  SELECT oi.product_id,
         o.user_id,
         COUNT(DISTINCT o.id) AS orders_count
  FROM order_items AS oi
  JOIN orders AS o ON o.id = oi.order_id
  WHERE o.status = 'COMPLETED'
  GROUP BY oi.product_id, o.user_id
)
SELECT p.id,
       p.name,
       ROUND(
         100.0 * SUM(CASE WHEN pb.orders_count >= 2 THEN 1 ELSE 0 END) / COUNT(*),
         2
       ) AS repeat_purchase_rate
FROM product_buyers AS pb
JOIN products AS p ON p.id = pb.product_id
GROUP BY p.id, p.name
ORDER BY repeat_purchase_rate DESC;
```

4) **Find potential stockouts: products with 7-day average sales > current inventory.**
```sql
WITH last_7_days AS (
  SELECT oi.product_id,
         SUM(oi.quantity) AS qty_sold
  FROM order_items AS oi
  JOIN orders AS o ON o.id = oi.order_id
  WHERE o.status = 'COMPLETED'
    AND o.order_date >= CURRENT_DATE - INTERVAL '7 days'
  GROUP BY oi.product_id
)
SELECT p.id,
       p.name,
       COALESCE(l7.qty_sold, 0) / 7.0 AS avg_daily_sales,
       i.on_hand
FROM products AS p
LEFT JOIN last_7_days AS l7 ON l7.product_id = p.id
LEFT JOIN inventory AS i ON i.product_id = p.id
WHERE COALESCE(l7.qty_sold, 0) / 7.0 > COALESCE(i.on_hand, 0)
ORDER BY avg_daily_sales DESC;
```

5) **Revenue contribution by customer decile (NTILE for segmentation).**
```sql
WITH customer_spend AS (
  SELECT o.user_id,
         SUM(o.total_amount) AS total_spend
  FROM orders AS o
  WHERE o.status = 'COMPLETED'
  GROUP BY o.user_id
),
segmented AS (
  SELECT cs.*,
         NTILE(10) OVER (ORDER BY cs.total_spend DESC) AS spend_decile
  FROM customer_spend AS cs
)
SELECT spend_decile,
       COUNT(*) AS customers,
       SUM(total_spend) AS decile_revenue,
       ROUND(100.0 * SUM(total_spend) / SUM(SUM(total_spend)) OVER (), 2) AS pct_of_total
FROM segmented
GROUP BY spend_decile
ORDER BY spend_decile;
```

6) **Detect potentially fraudulent orders: multiple payments with failed status then success.**
```sql
WITH payment_stats AS (
  SELECT p.order_id,
         COUNT(*) AS total_payments,
         SUM(CASE WHEN p.status = 'FAILED' THEN 1 ELSE 0 END) AS failed_payments,
         SUM(CASE WHEN p.status = 'SUCCESS' THEN 1 ELSE 0 END) AS success_payments
  FROM payments AS p
  GROUP BY p.order_id
)
SELECT ps.order_id,
       ps.total_payments,
       ps.failed_payments,
       ps.success_payments
FROM payment_stats AS ps
WHERE ps.failed_payments >= 2
  AND ps.success_payments >= 1
ORDER BY ps.failed_payments DESC, ps.total_payments DESC;
```
