--explain analyse
select c.id,
       c.email,
       c.firstname,
       c.lastname
from
    secrets.customers as c
where
    (c.firstname ilike '%maëlle%' OR
     c.lastname ilike '%maëlle%' OR
     c.email ilike '%maëlle%')
  AND
    (c.firstname ilike '%mor%' OR
     c.lastname ilike '%mor%' OR
     c.email ilike '%mor%')
order by
    email, id asc

