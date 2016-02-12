#!/bin/csh
foreach N (3 4 5 6 7)
   foreach I (1 2 3 4 5)
      ./Gen $N "ex_"$N"."$I
   end 
end

