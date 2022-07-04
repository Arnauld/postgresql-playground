-- explain analyse
select c.id,
       c.email,
       c.firstname,
       c.lastname,
       similarity
from
    secrets.customers as c,
    similarity('maëlle mor', lastname || ' ' || firstname || ' ' || email) as similarity
where
    similarity > 0.2
order by
    similarity desc,
    email, id asc

