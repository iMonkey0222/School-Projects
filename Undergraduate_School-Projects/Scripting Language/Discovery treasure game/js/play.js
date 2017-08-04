//This function is to get all available cells in the grid
function availableCells(){
  var aTags = document.getElementsByTagName('a');
  var avaCells= new Array();
  for (var i = 0; i < aTags.length; i++) {
    if (aTags[i].value==null) {avaCells.push(aTags[i]);}
  }
  return avaCells;
}
function locateMyRob(){
  var aTags = document.getElementsByTagName('a'); var myRobCell;
  for (var i = 0; i <aTags.length; i++) {
    if (aTags[i].value=='u') {myRobCell=aTags[i]}
  }
  return myRobCell;
}
function checkLeftTreasure(){
  var aTags = document.getElementsByTagName('a');
  var leftTreasure = new Array();
  for (var i = 0; i <aTags.length; i++) {
    var v = parseInt(aTags[i].value);
    if (v >=1 && v<=9) {leftTreasure.push(aTags[i]);}
  }
  return leftTreasure;
}

function preparePlay(){
  if (this.ableToPlay == true) {
    if (checkLeftTreasure().length) {
      var emptyCells = availableCells();
      if (emptyCells.length) {
        //randomly place a computer robot
        // set random element as computer robot element
        var comRob = emptyCells[Math.floor(Math.random()*emptyCells.length)];
        var myRobCell = locateMyRob();
        // insert computer robot image to specific cell
        loadImage(comRob,this.images[12],'c'); // c is the value means computer
        // if neither user's robot nor computer robot are able to move, then end game
        if (UserCheckAround(myRobCell.id) == false && ComCheckAroundCells(comRob.id).length==0) {
          endGame(); //end game
        }else{
          // if at least one of roobots can move, then start to paly
          var size = this.size; var d;
          var uTurn = true; var cTurn = false; 
          play(myRobCell,comRob,uTurn,cTurn);   // call play function to start game by user first
        }
      }else{     
        endGame(); // there is no empty cells for robots to move
      }
    }else{
      endGame(); // there is no treasure in grid
    }
  }
}

function play(myRobCell,comRob,uTurn,cTurn){
   // check end game each time play() is called
  if (UserCheckAround(myRobCell.id) == false && 
    ComCheckAroundCells(comRob.id).length==0) {
    endGame(); //both robots cannot move any more, then end game
  }else{
    document.addEventListener('keydown',function (event){
      if(uTurn) {    
        var key = event.which||event.keyCode; // compatiable to all browsers
        switch(key){
        case 87: d=-size;break;  // W:up
        case 88: d=size;break;   // S:down
        case 65: d=-1;break;     // A:left
        case 68: d=1;break;      // D:right
        default: alert("Error: Please type 'a','d','w','x'.");}
        myRobCell = myRobMove(myRobCell,d); // update myRobot cell
        uTurn = false;cTurn = true;
        if(checkLeftTreasure().length){
          // if there exists treasure in the grid, then call comRobMove function to let computer move
          comRobMove(myRobCell,comRob,uTurn,cTurn);
        }else{          
          endGame();// if there is no treasure left, then end game
        }           
      }
    },false);
  }
}

// This function is to clear current cell image and value,load image to and update value of new cell
function clearInsert(ele,id){
  var newCellEle=document.getElementById(id); // get the new cell element
  var v = newCellEle.value;
  if (v=='o' || v=='c') {
    // alert error message when user robot try to move on a cell occupied either by an obstable or by the computer's robot
    alert("Error: This is "+ (v=='o' ? 'obstacle' : 'computer robot'));
    return ele; // return itself
  }else{
    var n = parseInt(v);
    if (n>=1 && n<=9){
      if (ele.value == 'u') {
        // if now is user turn
        this.URscore+=n;                             // update user robot score;
        this.USContainer.textContent = this.URscore; // update user score container information
      }else if(ele.value =='c'){
        // if now is computer turn
        this.CRscore = this.CRscore+n;              // update computer robot score
        this.CSContainer.textContent =this.CRscore; // update computer score container information
      }
    }
    ele.innerHTML ='';         // clear current cell's image
    newCellEle.innerHTML = ''; // clear new cell image
    loadImage(newCellEle,(ele.value == 'u') ? this.images[10] : this.images[12],ele.value); // load image on new cell
    ele.value ='';             // clear the previous cell value
    return newCellEle;         // return the new cell Elemnet of my robot
  }
}



