// This function is used to end the game and display outcome.
function endGame(){
	var r = this.round; // get global round value
	this.ableToPlay = false;
  if (this.URscore == this.CRscore) {alert("Outcome\nRound "+r+"Draw");}
  else if (this.URscore > this.CRscore) {alert("Outcome\nRound "+r+": Your Robot Win!");}
  else{alert("Outcome\nRound "+r+": Computer Robot Win!");}
}