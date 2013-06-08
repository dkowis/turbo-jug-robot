-- Enforce only one meeting per day
alter table "MEETINGS" add constraint "UNIQUE_MEETING" unique ("MEETING_DATE")