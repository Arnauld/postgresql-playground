-- explain analyse
select c.id,
       c.email,
       c.firstname,
       c.lastname,
       c.phone,
       fullname_similarity,
       email_similarity,
       phone_similarity
from
    customers as c,
    similarity('0660809812', lastname || ' ' || firstname) as fullname_similarity,
    similarity('0660809812', email)                        as email_similarity,
    similarity('0660809812', phone)                        as phone_similarity,
where
    fullname_similarity > 0 or email_similarity > 0 or phone_similarity > 0
order by
    fullname_similarity desc,
    email_similarity desc,
    phone_similarity desc,
    email, id asc

