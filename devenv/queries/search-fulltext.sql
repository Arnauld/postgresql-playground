explain analyse
select c.id,
       ts_rank(document, pquery) as rank,
       ts_rank(fullname, pquery) as rank_fullname,
       -- query,
       pquery,
       document,
       -- fullname,
       c.email,
       c.firstname,
       c.lastname
       -- ,c.localized_labels,
from
    secrets.customers as c,
    to_tsquery('fr', 'mor|maëlle') as pquery,
    to_tsvector('fr', firstname || ' ' || lastname || ' ' || email) as document,
    to_tsvector('fr', firstname || ' ' || lastname) as fullname
where
        pquery @@ document
order by
    rank desc,
    email, id asc NULLS LAST;


