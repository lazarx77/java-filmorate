-- заполнение таблицы MPA

MERGE INTO MPA AS target
USING (SELECT 1 AS MPA_ID, 'G' AS MPA_NAME) AS source
ON target.MPA_ID = source.MPA_ID
WHEN MATCHED THEN
    UPDATE SET target.MPA_NAME = source.MPA_NAME
WHEN NOT MATCHED THEN
    INSERT (MPA_ID, MPA_NAME) VALUES (source.MPA_ID, source.MPA_NAME);

MERGE INTO MPA AS target
USING (SELECT 2 AS MPA_ID, 'PG' AS MPA_NAME) AS source
ON target.MPA_ID = source.MPA_ID
WHEN MATCHED THEN
    UPDATE SET target.MPA_NAME = source.MPA_NAME
WHEN NOT MATCHED THEN
    INSERT (MPA_ID, MPA_NAME) VALUES (source.MPA_ID, source.MPA_NAME);

MERGE INTO MPA AS target
USING (SELECT 3 AS MPA_ID, 'PG-13' AS MPA_NAME) AS source
ON target.MPA_ID = source.MPA_ID
WHEN MATCHED THEN
    UPDATE SET target.MPA_NAME = source.MPA_NAME
WHEN NOT MATCHED THEN
    INSERT (MPA_ID, MPA_NAME) VALUES (source.MPA_ID, source.MPA_NAME);

MERGE INTO MPA AS target
USING (SELECT 4 AS MPA_ID, 'R'
AS MPA_NAME) AS source
ON target.MPA_ID = source.MPA_ID
WHEN MATCHED THEN
    UPDATE SET target.MPA_NAME = source.MPA_NAME
WHEN NOT MATCHED THEN
    INSERT (MPA_ID, MPA_NAME) VALUES (source.MPA_ID, source.MPA_NAME);

MERGE INTO MPA AS target
USING (SELECT 5 AS MPA_ID, 'NC-17' AS MPA_NAME) AS source
ON target.MPA_ID = source.MPA_ID
WHEN MATCHED THEN
    UPDATE SET target.MPA_NAME = source.MPA_NAME
WHEN NOT MATCHED THEN
    INSERT (MPA_ID, MPA_NAME) VALUES (source.MPA_ID, source.MPA_NAME);

-- заполнение таблицы GENRES

MERGE INTO GENRES AS target
USING (SELECT 1 AS GENRE_ID, 'Комедия' AS GENRE_NAME) AS source
ON target.GENRE_ID = source.GENRE_ID
WHEN MATCHED THEN
    UPDATE SET target.GENRE_NAME = source.GENRE_NAME
WHEN NOT MATCHED THEN
    INSERT (GENRE_ID, GENRE_NAME) VALUES (source.GENRE_ID, source.GENRE_NAME);

MERGE INTO GENRES AS target
USING (SELECT 2 AS GENRE_ID, 'Драма' AS GENRE_NAME) AS source
ON target.GENRE_ID = source.GENRE_ID
WHEN MATCHED THEN
    UPDATE SET target.GENRE_NAME = source.GENRE_NAME
WHEN NOT MATCHED THEN
    INSERT (GENRE_ID, GENRE_NAME) VALUES (source.GENRE_ID, source.GENRE_NAME);

MERGE INTO GENRES AS target
USING (SELECT 3 AS GENRE_ID, 'Мультфильм' AS GENRE_NAME) AS source
ON target.GENRE_ID = source.GENRE_ID
WHEN MATCHED THEN
    UPDATE SET target.GENRE_NAME = source.GENRE_NAME
WHEN NOT MATCHED THEN
    INSERT (GENRE_ID, GENRE_NAME) VALUES (source.GENRE_ID, source.GENRE_NAME);

MERGE INTO GENRES AS target
USING (SELECT 4 AS GENRE_ID, 'Триллер' AS GENRE_NAME) AS source
ON target.GENRE_ID = source.GENRE_ID
WHEN MATCHED THEN
    UPDATE SET target.GENRE_NAME = source.GENRE_NAME
WHEN NOT MATCHED THEN
    INSERT (GENRE_ID, GENRE_NAME) VALUES (source.GENRE_ID, source.GENRE_NAME);

MERGE INTO GENRES AS target
USING (SELECT 5 AS GENRE_ID, 'Документальный' AS GENRE_NAME) AS source
ON target.GENRE_ID = source.GENRE_ID
WHEN MATCHED THEN
    UPDATE SET target.GENRE_NAME = source.GENRE_NAME
WHEN NOT MATCHED THEN
    INSERT (GENRE_ID, GENRE_NAME) VALUES (source.GENRE_ID, source.GENRE_NAME);

MERGE INTO GENRES AS target
USING (SELECT 6 AS GENRE_ID, 'Боевик' AS GENRE_NAME) AS source
ON target.GENRE_ID = source.GENRE_ID
WHEN MATCHED THEN
    UPDATE SET target.GENRE_NAME = source.GENRE_NAME
WHEN NOT MATCHED THEN
    INSERT (GENRE_ID, GENRE_NAME) VALUES (source.GENRE_ID, source.GENRE_NAME);
