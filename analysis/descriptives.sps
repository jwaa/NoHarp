SORT CASES  BY condition.
SPLIT FILE SEPARATE BY condition.

EXAMINE VARIABLES=keyboard_accuracy keyboard_speed_average keyboard_distance keyboard_block_moves 
    keyboard_rotations keyboard_Q12_satisfied keyboard_system_usefulness keyboard_interface_quality 
    leap_accuracy leap_speed_average leap_distance leap_block_moves leap_rotations leap_Q12_satisfied 
    leap_system_usefulness leap_interface_quality
  /PLOT NONE
  /STATISTICS DESCRIPTIVES
  /CINTERVAL 95
  /MISSING LISTWISE
  /NOTOTAL.

SPLIT FILE OFF.
SORT CASES BY id.




