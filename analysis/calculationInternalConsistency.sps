
DATASET ACTIVATE DataSet1.
RELIABILITY
  /VARIABLES=keyboard_Q1_easy_to_use keyboard_Q2_simple_to_use keyboard_Q3_complete_effectively 
    keyboard_Q4_complete_quickly keyboard_Q5_complete_efficiently keyboard_Q6_comfortable 
    keyboard_Q7_easy_to_learn keyboard_Q8_quickly_productive
  /SCALE('keyboard_system_usefulness') ALL
  /MODEL=ALPHA
  /STATISTICS=DESCRIPTIVE SCALE CORR
  /SUMMARY=TOTAL.

RELIABILITY
  /VARIABLES=keyboard_Q9_interface_pleasent keyboard_Q10_interface_liked keyboard_Q11_all_functions
  /SCALE('keyboard_interface_quality') ALL
  /MODEL=ALPHA
  /STATISTICS=DESCRIPTIVE SCALE CORR
  /SUMMARY=TOTAL.

RELIABILITY
  /VARIABLES=leap_Q1_easy_to_use leap_Q2_simple_to_use leap_Q3_complete_effectively 
    leap_Q4_complete_quickly leap_Q5_complete_efficiently leap_Q6_comfortable 
    leap_Q7_easy_to_learn leap_Q8_quickly_productive
  /SCALE('leap_system_usefulness') ALL
  /MODEL=ALPHA
  /STATISTICS=DESCRIPTIVE SCALE CORR
  /SUMMARY=TOTAL.

RELIABILITY
  /VARIABLES=leap_Q9_interface_pleasent leap_Q10_interface_liked leap_Q11_all_functions
  /SCALE('leap_interface_quality') ALL
  /MODEL=ALPHA
  /STATISTICS=DESCRIPTIVE SCALE CORR
  /SUMMARY=TOTAL.
