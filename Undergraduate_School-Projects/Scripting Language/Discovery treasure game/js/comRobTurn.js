function comRobMove(myRobCell,comRob,uTurn,cTurn){
  if (UserCheckAround(myRobCell.id) == false && 
    ComCheckAroundCells(comRob.id).length==0) {
    // if neither user's robot nor computer robot are able to move, end game
    endGame(); //end game
  }else{
    // if at least one of robots can move, then continue game
    if (cTurn) {
      // if it is computer turn
      var id = parseInt(comRob.id); 
      var aroundIds = new Array(); 
      aroundIds = ComCheckAroundCells(id);
      
      if (aroundIds.length) {
        // if exist around available cells:aroundIds length>0
        var newComId = aroundIds[Math.floor(Math.random()*aroundIds.length)]
        comRob = clearInsert(comRob,newComId); // call clearInsert function
        cTurn = false; uTurn = true;           // update boolean turn value
        
        if (checkLeftTreasure().length) {
          // if there is treasure left in the grid
          this.round +=1;                               // update round infomation
          this.roundContainer.textContent = this.round; // update round container information
          play(myRobCell,comRob,uTurn,cTurn);           // start next round
        }else{         
          endGame(); // if there is no treasure left in the grid
        }
      }else{
        // if computer robot can not move any more, 
        this.round +=1;                               // update round infomation
        this.roundContainer.textContent = this.round; // update round container information
        cTurn = false; uTurn = true;                  // update boolean turn value
        play(myRobCell,comRob,uTurn,cTurn);           // start next round
      } 
    }
  }
}

// This function is used to find all around cells' id
function ComCheckAroundCells(id){
  id = parseInt(id);       // parse id from string type to int type
  var s = this.size; 
  var r=Math.floor(id/s);  // row of current cell
  var c=id%s;              // column of current cell
  var new_id = new Array();// stores all posibile cells
  new_id=[id-(s+1),id-s,id-s+1,id-1,id+1,id+s-1,id+s,id+s+1];
  // all posibile cells' id: LUp,Up,RUp,L,R,LDn,Dn,RDn
  var aroundIds = new Array(); var map = new Array();
  if (c!=0 && c!=1 && r!=0 && r!=s-1) {map=[0,1,2,3,4,5,6,7];}
  // on right edge of grid
  else if (c==0 && r>=2 && r<=s-1) {map=[0,1,3,5,6];}
  // on left edge of grid
  else if (c==1 && r>=1 && r<=s-2) {map=[1,2,4,6,7];}
  // on up edge of grid
  else if (r==0 && c>=2 && c<=s-1) {map=[3,4,5,6,7];}
  // on down edge of grid
  else if (r==s-1 && c>=2 && c<=s-1) {map=[0,1,2,3,4];}
  // left up corner
  else if (r==0 && c==1) {map=[4,6,7];}
  // right up corner
  else if (r==1 && c==0) {map=[3,5,6];}
  // left down corner
  else if (r==s-1 && c==1) {map=[1,2,4];}
  // right down corner
  else if (r==s && c==0) {map =[0,1,3];}

  if (map.length) {
    // if map array contains element
    for (var i = 0; i < map.length; i++) {
      aroundIds.push(new_id[map[i]]); // match the value in new_id[] and add it (around comRob cells' id) to the array aroundIds 
    }
  } 
  // fliter the available cells id by calling ComCheckCellsValue function
  aroundIds = ComCheckCellsValue(aroundIds);
  return aroundIds;
}

// check around computer robot cells value
function ComCheckCellsValue(ids){
  var treasure_ids = new Array(); // this array stores treasure cells' id
  for (var i = 0; i < ids.length; i++) {
    var ele = document.getElementById(ids[i]);
    if (ele.value ==='o' || ele.value ==='u') {
      ids.splice(i,1); // delete the ids where is obstable and user robot
      i = i-1;         // after delete the element the length of array ids will decrease 1, so decrease index as well avoid skipping the next element
    } 
    else if (parseInt(ele.value)>=1 && parseInt(ele.value)<=9) {
      treasure_ids.push(ids[i]);
    }
  }
  // if around cells contain treasure, then return treasure list, otherwise, return original list without obstable and user robot cells' id
  return (treasure_ids.length) ? treasure_ids : ids; 
}