-- explain analyse
select c.id,
       c.email,
       c.firstname,
       c.lastname,
       fullname_similarity,
       email_similarity
       -- ,show_trgm(lastname || ' ' || firstname)
       -- ,show_trgm(email)
from
    secrets.customers as c,
    similarity('maëlle mor', lastname || ' ' || firstname) as fullname_similarity,
    similarity('maëlle mor', email) as email_similarity
where
    fullname_similarity > 0.2 or email_similarity > 0.2
order by
    fullname_similarity desc,
    email_similarity desc,
    email, id asc

