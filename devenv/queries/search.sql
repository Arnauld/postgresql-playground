-- explain analyse
select c.id,
       ts_rank(document, query) as rank,
       ts_rank(fullname, query) as rank_fullname,
       -- query,
       pquery,
       document,
       -- fullname,
       c.email,
       c.firstname,
       c.lastname,
       -- c.localized_labels,
       similarity
from
    secrets.customers as c,
    to_tsquery('fr', 'maëlle') as query,
    to_tsquery('fr', 'maëlle|mor') as pquery,
    to_tsvector('fr', firstname || ' ' || lastname || ' ' || email) as document,
    to_tsvector('fr', firstname || ' ' || lastname) as fullname,
    similarity('maëlle mor', firstname || ' ' || lastname || ' ' || email) as similarity
where
        pquery @@ document
   or similarity > 0.7
order by
    rank desc,
    similarity desc,
    email, id asc NULLS LAST;


