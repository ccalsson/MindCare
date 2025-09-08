-- Seed data for MindCare (idempotent)

-- Meditation sessions
insert into public.meditation_sessions (id, title, description, duration, category, audio_url, image_url, is_premium)
select gen_random_uuid(), 'Sueño profundo', 'Relajación guiada para mejorar el descanso nocturno', 900,
       'MeditationCategory.sleep', 'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', false
where not exists (select 1 from public.meditation_sessions where title='Sueño profundo');

insert into public.meditation_sessions (id, title, description, duration, category, audio_url, image_url, is_premium)
select gen_random_uuid(), 'Enfoque máximo', 'Meditación breve para maximizar tu concentración', 600,
       'MeditationCategory.focus', 'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', false
where not exists (select 1 from public.meditation_sessions where title='Enfoque máximo');

insert into public.meditation_sessions (id, title, description, duration, category, audio_url, image_url, is_premium)
select gen_random_uuid(), 'Ansiedad bajo control', 'Respiraciones profundas y visualización calmante', 720,
       'MeditationCategory.anxiety', 'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', false
where not exists (select 1 from public.meditation_sessions where title='Ansiedad bajo control');

insert into public.meditation_sessions (id, title, description, duration, category, audio_url, image_url, is_premium)
select gen_random_uuid(), 'Mindfulness inicial', 'Introducción práctica a la atención plena', 480,
       'MeditationCategory.beginner', 'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', false
where not exists (select 1 from public.meditation_sessions where title='Mindfulness inicial');

insert into public.meditation_sessions (id, title, description, duration, category, audio_url, image_url, is_premium)
select gen_random_uuid(), 'Masterclass Premium', 'Sesión avanzada para usuarios premium', 1200,
       'MeditationCategory.mindfulness', 'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', true
where not exists (select 1 from public.meditation_sessions where title='Masterclass Premium');

-- Audio resources
insert into public.audio_resources (id, title, url, thumbnail_url, duration, is_premium)
select gen_random_uuid(), 'Bosque lluvioso',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', 1800, false
where not exists (select 1 from public.audio_resources where title='Bosque lluvioso');

insert into public.audio_resources (id, title, url, thumbnail_url, duration, is_premium)
select gen_random_uuid(), 'Olas del mar',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', 1800, false
where not exists (select 1 from public.audio_resources where title='Olas del mar');

insert into public.audio_resources (id, title, url, thumbnail_url, duration, is_premium)
select gen_random_uuid(), 'Campanas tibetanas (Premium)',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/audio/placeholder.mp3',
       'https://storage.googleapis.com/mindcare-745ca.firebasestorage.app/images/placeholder.png', 1200, true
where not exists (select 1 from public.audio_resources where title='Campanas tibetanas (Premium)');

-- Premium content
insert into public.premium_content (id, title, description, type, tags, metadata, release_date)
select gen_random_uuid(), 'Mindfulness para líderes', 'Masterclass exclusiva con expertos', 'masterclass',
       array['liderazgo','mindfulness'], '{"level":"advanced"}', now()
where not exists (select 1 from public.premium_content where title='Mindfulness para líderes');

