DATASET ACTIVATE DataSet1.
EXAMINE VARIABLES=keyboard_speed_average leap_speed_average keyboard_accuracy leap_accuracy keyboard_distance leap_distance keyboard_block_moves leap_block_moves keyboard_rotations leap_rotations
  /PLOT BOXPLOT HISTOGRAM NPPLOT
  /COMPARE GROUPS
  /STATISTICS DESCRIPTIVES EXTREME
  /CINTERVAL 95
  /MISSING LISTWISE
  /NOTOTAL.

EXAMINE VARIABLES=keyboard_system_usefulness leap_system_usefulness keyboard_interface_quality leap_interface_quality keyboard_Q12_satisfied leap_Q12_satisfied
  /PLOT BOXPLOT HISTOGRAM NPPLOT
  /COMPARE GROUPS
  /STATISTICS DESCRIPTIVES EXTREME
  /CINTERVAL 95
  /MISSING LISTWISE
  /NOTOTAL.

