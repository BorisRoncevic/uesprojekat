INSERT INTO users(username, email, password, member_since, date_of_birth, phone_number, address, city, role, profile_status)
SELECT 'admin', 'admin@gmail.com', '$2a$10$luqEVAdjiGEVJb.lW5qbYuL.yAkYPZdJxZkKz5lGlUaQZzJFhi.ju', '2025-06-06', '2002-05-05', '0622222222', 'Miloje Mili 19', 'Bogojevo', 'ADMIN', 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin' OR email = 'admin@gmail.com');
-- Admin123.
INSERT INTO users(username, email, password, member_since, date_of_birth, phone_number, address, city, role, profile_status)
SELECT 'vukasin', 'vukasin@gmail.com', '$2a$10$luqEVAdjiGEVJb.lW5qbYuL.yAkYPZdJxZkKz5lGlUaQZzJFhi.ju', '2025-06-06', '2002-05-05', '0633333333', 'Miloje Milic 19', 'Bogojevo', 'MEMBER', 1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'vukasin' OR email = 'vukasin@gmail.com');
-- Admin123.
INSERT INTO users(username, email, password, member_since, date_of_birth, phone_number, address, city, role, profile_status)
SELECT 'vuka1sin', 'vu1kasin@gmail.com', '$2a$10$luqEVAdjiGEVJb.lW5qbYuL.yAkYPZdJxZkKz5lGlUaQZzJFhi.ju', '2025-06-06',null, null, null, null, 'MEMBER', 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'vuka1sin' OR email = 'vu1kasin@gmail.com');
-- Admin123.

INSERT INTO images(event_id, user_id, venue_id, path)
SELECT null, 2, null, 'https://picsum.photos/800/600'
WHERE NOT EXISTS (
    SELECT 1 FROM images
    WHERE event_id IS NULL AND user_id = 2 AND venue_id IS NULL AND path = 'https://picsum.photos/800/600'
);

INSERT INTO venues (name, description, address, average_rating, type, created_at)
SELECT v.name, v.description, v.address, v.average_rating, v.type, v.created_at::date
FROM (VALUES
    ('The Grand Oak Theater', 'Historic theater showing classic films and live performances.', '123 Broadway, Starlight City', 4.8, 4, '2022-01-15'),
    ('Sunset Brews Cafe', 'Cozy cafe with artisanal coffee and a wide selection of craft beers.', '456 Market St, Dawnsville', 4.5, 1, '2022-03-20'),
    ('Le Monde Bistro', 'Authentic French cuisine in a charming, rustic setting.', '789 Vineyard Ave, Metroburg', 4.7, 3, '2022-05-10'),
    ('Metropolis Modern Art Museum', 'A vast collection of contemporary and modern art from around the world.', '101 Museum Plaza, Metroburg', 4.9, 7, '2022-06-01'),
    ('The Alchemist''s Bar', 'An inventive cocktail bar with a speakeasy vibe.', '321 Alley Way, Starlight City', 4.6, 1, '2022-08-25'),
    ('Central City Park', 'A beautiful urban oasis with walking trails, a lake, and botanical gardens.', '500 Park Dr, Metroburg', 4.4, 0, '2022-09-05'),
    ('Club Neon', 'The city''s premier destination for electronic music and nightlife.', '999 Beat St, Metroburg', 4.1, 2, '2022-11-18'),
    ('The Canvas Gallery', 'A small, independent gallery featuring local artists.', '246 Art Walk, Dawnsville', 4.3, 7, '2023-01-30'),
    ('The Reader''s Nook', 'A quiet bookstore combined with a delightful cafe, perfect for an afternoon.', '111 Page Turner Ln, Starlight City', 4.8, 3, '2023-02-14'),
    ('Titans Stadium', 'Home of the Metroburg Titans, hosting major sporting events and concerts.', '777 Victory Rd, Metroburg', 4.5, 6, '2023-03-01'),
    ('Nonna''s Pizzeria', 'Family-owned pizzeria serving traditional recipes for over 50 years.', '555 Tomato St, Dawnsville', 4.7, 3, '2023-04-12'),
    ('Emerald Gardens', 'A lush botanical garden featuring rare and exotic plants from every continent.', '800 Flower Ave, Starlight City', 4.9, 0, '2023-05-22'),
    ('The Skyview Lounge', 'A chic rooftop bar offering stunning panoramic views of the city skyline.', '400 Pinnacle Tower, Metroburg', 4.6, 5, '2023-06-15'),
    ('Oceanic Wonders Aquarium', 'An immersive underwater experience with thousands of marine species.', '123 Waterfront Pier, Dawnsville', 4.7, 7, '2023-07-01'),
    ('The Grand Public Library', 'A historic landmark and repository of knowledge, open to all.', '1 Grand Circle, Metroburg', 4.9, 0, '2023-08-08')
) AS v(name, description, address, average_rating, type, created_at)
WHERE NOT EXISTS (SELECT 1 FROM venues WHERE venues.name = v.name);

INSERT INTO events (name, description, event_type, date, address, price, recurrent, venue_id)
SELECT e.name, e.description, e.event_type, e.date::date, e.address, e.price, e.recurrent, e.venue_id
FROM (VALUES
    ('Weekly Jazz Night', 'An evening of smooth jazz featuring local artists. Full bar and dinner menu available.', 2, '2025-10-01', '123 Melody Lane, Musicville, USA', 15.50, true, 1),
    ('Weekly Jazz Night', 'An evening of smooth jazz featuring local artists. Full bar and dinner menu available.', 2, '2025-10-06', '123 Melody Lane, Musicville, USA', 15.50, true, 1),
    ('Weekly Jazz Night', 'An evening of smooth jazz featuring local artists. Full bar and dinner menu available.', 2, '2025-10-10', '123 Melody Lane, Musicville, USA', 15.50, true, 1),
    ('Weekly Jazz Night', 'An evening of smooth jazz featuring local artists. Full bar and dinner menu available.', 2, '2025-10-13', '123 Melody Lane, Musicville, USA', 15.50, true, 1),
    ('Weekly Jazz Night', 'An evening of smooth jazz featuring local artists. Full bar and dinner menu available.', 0, '2025-10-16', '123 Melody Lane, Musicville, USA', 15.50, true, 1),
    ('Coconut', 'An evening of smooth jazz featuring local artists. Full bar and dinner menu available.', 3, '2025-10-16', '123 Melody Lane, Musicville, USA', 15.50, true, 1),
    ('Weekly Jazz Night', 'An evening of smooth jazz featuring local artists. Full bar and dinner menu available.', 2, '2025-10-9', '123 Melody Lane, Musicville, USA', 15.50, true, 1)
) AS e(name, description, event_type, date, address, price, recurrent, venue_id)
WHERE NOT EXISTS (
    SELECT 1 FROM events
    WHERE events.name = e.name
      AND events.event_type = e.event_type
      AND events.date = e.date::date
      AND events.venue_id = e.venue_id
);

INSERT INTO images (path, user_id, venue_id, event_id)
SELECT i.path, i.user_id::bigint, i.venue_id::bigint, i.event_id::bigint
FROM (VALUES
    ('https://picsum.photos/800/600', null, 1, null),
    ('https://picsum.photos/800/600', null, 2, null),
    ('https://picsum.photos/800/600', null, 3, null),
    ('https://picsum.photos/800/600', null, 4, null),
    ('https://picsum.photos/800/600', null, 5, null),
    ('https://picsum.photos/800/600', null, 6, null),
    ('https://picsum.photos/800/600', null, 7, null),
    ('https://picsum.photos/800/600', null, 8, null),
    ('https://picsum.photos/800/600', null, 9, null),
    ('https://picsum.photos/800/600', null, 10, null),
    ('https://picsum.photos/800/600', null, 11, null),
    ('https://picsum.photos/800/600', null, 12, null),
    ('https://picsum.photos/800/600', null, 13, null),
    ('https://picsum.photos/800/600', null, 14, null),
    ('https://picsum.photos/800/600', null, 15, null),
    ('https://picsum.photos/800/600', null, null, 1),
    ('https://picsum.photos/800/600', null, null, 2),
    ('https://picsum.photos/800/600', null, null, 3),
    ('https://picsum.photos/800/600', null, null, 4),
    ('https://picsum.photos/800/600', null, null, 5),
    ('https://picsum.photos/800/600', null, null, 6),
    ('https://picsum.photos/800/600', null, null, 7)
) AS i(path, user_id, venue_id, event_id)
WHERE NOT EXISTS (
    SELECT 1 FROM images
    WHERE images.path = i.path
      AND images.user_id IS NOT DISTINCT FROM i.user_id::bigint
      AND images.venue_id IS NOT DISTINCT FROM i.venue_id::bigint
      AND images.event_id IS NOT DISTINCT FROM i.event_id::bigint
);


INSERT INTO manages(start_date, manager_id, venue_id)
SELECT '2025-06-06'::date, 2, 1
WHERE NOT EXISTS (SELECT 1 FROM manages WHERE manager_id = 2 AND venue_id = 1);

-- INSERT INTO comments (content, created_at, user_id, parent_id)
-- VALUES ('This was an absolutely phenomenal show! The trumpet solos were out of this world.', '2023-10-28', 3, null);

-- INSERT INTO comments (content, created_at, user_id, parent_id)
-- VALUES ('Thank you so much for your kind words, vuka1sin! We are thrilled you enjoyed the tribute. Hope to see you again soon!', '2023-10-29', 2, 13);


-- INSERT INTO reviews (status, created_at, user_id, event_id, venue_id, comment_id)
-- VALUES (2, '2023-10-28', 3, 1, 1, 13);


-- INSERT INTO ratings (performance, ambient, venue, overall_impression, review_id)
-- VALUES (5, 4, 5, 5, 7);
