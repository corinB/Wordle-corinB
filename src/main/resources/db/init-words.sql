INSERT INTO words ("value")
VALUES
  ('apple'),
  ('grape'),
  ('peach'),
  ('melon'),
  ('lemon'),
  ('mango'),
  ('berry'),
  ('bread'),
  ('chair'),
  ('table'),
  ('house'),
  ('water'),
  ('light'),
  ('sound'),
  ('world'),
  ('plant'),
  ('green'),
  ('black'),
  ('white'),
  ('stone'),
  ('cocoa'),
  ('alarm'),
  ('banal'),
  ('canal'),
  ('naval'),
  ('papal'),
  ('cabal'),
  ('basal'),
  ('gamma'),
  ('mamma'),
  ('manga'),
  ('mania'),
  ('panda'),
  ('salsa'),
  ('karma'),
  ('kappa'),
  ('tanga'),
  ('saran'),
  ('malam'),
  ('madam'),
  ('magma'),
  ('bazar'),
  ('radar'),
  ('salad'),
  ('varia'),
  ('zamia'),
  ('tapas')
ON CONFLICT ("value") DO NOTHING;

INSERT INTO wordle_games (correct_word_id, start_at, end_at)
SELECT
  id,
  CURRENT_DATE::timestamp,
  (CURRENT_DATE + INTERVAL '1 day')::timestamp
FROM words
WHERE "value" = 'apple'
ON CONFLICT DO NOTHING;
