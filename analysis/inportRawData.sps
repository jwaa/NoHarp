

GET DATA
  /TYPE=TXT
  /FILE=
    "C:\Users\mikel_000\Documents\workspace\noharp-analysis\processed\74ab7824-23cf-40a8-afe1-4dea5ba"+
    "51d44.csv"
  /DELCASE=LINE
  /DELIMITERS=","
  /ARRANGEMENT=DELIMITED
  /FIRSTCASE=2
  /IMPORTCASE=ALL
  /VARIABLES=
  id F2.0
  keyboard_accuracy DOT18.0
  keyboard_speed_task_1 F5.0
  keyboard_speed_task_2 F5.0
  keyboard_speed_task_3 F5.0
  keyboard_distance DOT18.0
  keyboard_block_moves F2.0
  keyboard_horizontal_rotations F1.0
  keyboard_vertical_rotations F2.0
  leap_accuracy DOT19.0
  leap_speed_task_1 F6.0
  leap_speed_task_2 F6.0
  leap_speed_task_3 F6.0
  leap_distance DOT18.0
  leap_block_moves F2.0
  leap_horizontal_rotations F2.0
  leap_vertical_rotations F3.0.
CACHE.
EXECUTE.
DATASET NAME DataSet1 WINDOW=FRONT.

DATASET ACTIVATE DataSet1.
SORT CASES BY id(A).

