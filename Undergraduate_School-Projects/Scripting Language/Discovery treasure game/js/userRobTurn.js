// This function is move user robot
function myRobMove(element,distance){
  var id = parseInt(element.id);
    if (userWithinBound(id,distance)) {
      //If new element cell is within the grid, then store next move cell id to new_id. 
      new_id =id+distance;                // next move cell id
      return clearInsert(element,new_id); // call clearInsert() and return its return value.
    }else{
      // Else, alert outside bound reminder.
      alert("Error: You will be outside the grid.");
      return element;
    }
}

// This function is used to check whether user robot could move or not
function UserCheckAround(id){
  id = parseInt(id);        // parser string type id to integer type
  var s= this.size;         // the size of the grid
  var r=Math.floor(id/s);   // the row position of cell in grid
  var c=id%s;               // the column position of cell in grid
  var around = new Array(); // store all posibile ids of cells around user Robot
  var map = new Array();    // store the map index value of around
  around = [id-s,id-1,id+s,id+1] // up,left,down,right cell's id
  
  if (c!=0 && c!=1 && r!=0 && r!=s-1){ map = [0,1,2,3];} // center area
  else if (c==0 && r>=2 && r<=s-1){map = [0,1,2];} // right edge
  else if (c==1 && r>=1 && r<=s-2){map = [0,2,3];} // left edge
  else if (r==0 && c>=2 && c<=s-1){map = [1,2,3];} // up edge
  else if (r==s-1 && c>=2 && c<=s-1){map =[0,1,3];}// botom edge
  else if (r==0 && c==1){map=[2,3];}    // left up corner
  else if (r==s-1 && c==1){map=[0,3];}  // left bottom corner
  else if (r==1 && c==0){ map =[1,2];}  // right up corner
  else if (r==s && c==0){map = [0,1];}  // right bottom corner
  
  var unAvailable = new Array();
  // if all around cells are occupied by obstables or comRobot, return false 
  if (map.length) {
    for (var i = 0; i < map.length; i++) {
      var e = document.getElementById(around[map[i]]);
      // add occupied cell to unAvailable array
      if(e.value=== 'c' || e.value==='o'){unAvailable.push(e);}
    }
  }
  // compare the length of map and unAvilable, if equal, it means user robot has no place to move
  return !(unAvailable.length == map.length);
}

// This function is to check if current postion of robot is within the grid
function userWithinBound(curId,d){
  var s= this.size
  var r=Math.floor(curId/s)
  var c=curId%s
  return (c!=0 && c!=1 && r!=0 && r!=s-1)||
  (c==0 && r>=2 && r<=s-1 && d!==1) || 
  (c==1 && r>=1 && r<=s-2 && d!==-1) ||
  (r==0 && c>=2 && c<=s-1 && d!==-s) ||
  (r==s-1 && c>=2 && c<=s-1 && d!==s) ||
  (r==0 && c==1 && (d==1 || d==s)) ||
  (r==s-1 && c==1 && (d==1 || d==-s)) ||
  (r==1 && c==0 && (d==-1 || d==s)) ||
  (r==s && c==0 && (d==-1 || d==-s));
}