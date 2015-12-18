-- workaround: http://stackoverflow.com/questions/20773805/postgresql-enum-and-character-varying-updating

create function publishing_status_cast(varchar) returns publishing_status as $$
  select case $1
    when 'private' then 'private'::publishing_status
    when 'published' then 'published'::publishing_status
    when 'suspended' then 'suspended'::publishing_status
  end;
$$ language sql;

create cast (varchar as publishing_status) with function publishing_status_cast(varchar) as assignment;

create function video_service_cast(varchar) returns video_service as $$
  select case $1
    when 'youtube' then 'youtube'::video_service
  end;
$$ language sql;

create cast (varchar as video_service) with function video_service_cast(varchar) as assignment;

create function editorial_right_cast(varchar) returns editorial_right as $$
  select case $1
    when 'selected' then 'selected'::editorial_right
    when 'classmate' then 'classmate'::editorial_right
    when 'cohort' then 'cohort'::editorial_right
    when 'all' then 'all'::editorial_right
  end;
$$ language sql;

create cast (varchar as editorial_right) with function editorial_right_cast(varchar) as assignment;

create function fixed_content_type_cast(varchar) returns fixed_content_type as $$
  select case $1
    when 'news' then 'news'::fixed_content_type
    when 'about' then 'about'::fixed_content_type
    when 'contact' then 'contact'::fixed_content_type
  end;
$$ language sql;

create cast (varchar as fixed_content_type) with function fixed_content_type_cast(varchar) as assignment;
