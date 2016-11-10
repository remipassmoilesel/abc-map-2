CREATE TABLE abcmap_layer_index (
  ly_id      CHAR(50) PRIMARY KEY NOT NULL,
  ly_name    CHAR(100)            NOT NULL,
  ly_visible BOOLEAN              NOT NULL,
  ly_zindex  INT                  NOT NULL,
  ly_type    CHAR(50)             NOT NULL
);