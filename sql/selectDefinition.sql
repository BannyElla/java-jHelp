script=select d.id, d.definition, t.id as trem_id from tblDefinitions d join tblTerms t on (d.TERM_ID = t.ID) where lower(t.TERM) like lower(?)