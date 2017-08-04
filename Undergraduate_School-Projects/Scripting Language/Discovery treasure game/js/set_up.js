  function preLoadImage(){
    this.images[1] = "img/1.png";
    this.images[2] = "img/2.png";
    this.images[3] = "img/3.png";
    this.images[4] = "img/4.png";
    this.images[5] = 'img/5.png';
    this.images[6] = 'img/6.png';
    this.images[7] = 'img/7.png';
    this.images[8] = 'img/8.png';
    this.images[9] = 'img/9.png';
    this.images[10] = 'img/user-robot.jpg';
    this.images[11] = 'img/obstacle.jpg';
    this.images[12] = 'img/computer-robot.jpg';
  }

  function setUp(obj){
    preLoadImage();
    if (this.canSetUp== true) {
      if (obj.value ==null) {
        var value = prompt("You can \n1. Set up the treasures by typing a number between 1 and 9;\n2. Add obstacles by typing the letter 'o';\n3. Place your robot on this cell by typing the leter 'u'.\n","")
                
        if (parseInt(value)>=1 && parseInt(value)<=9){
          // if int type value of object is between 1 to 9, then insert image to corresponding cell
          loadImage(obj,this.images[value],value);
        }else if (value === 'u' && this.setRob == false) {
          // if user type 'u' and user robot has not been placed, then insert corresponding image
          loadImage(obj,this.images[10],value);
          this.setRob = true;  
        }else if (value==='o') {
          loadImage(obj,this.images[11],value);
        }else{
          // if user try to place the second robot, alert error message
          if (value === 'u' && this.setRob == true) {alert("Warning: You have already placed your robot!\n");}
          // if user click cancel button, then do nothing
          else if (value == '' || value == null){}
          // if user input unvalid input, alert error message
          else{alert("Error: unvalid input! \nNot among 1 to 9, 'o' and 'u'")}
        }
      }else{
        // if the object value is not undefined, then alert error messgae
        alert("Error: You cannot change the object placed on this cell!")
      }
    } 
  }
  // This function is used to insert image to specific cell
  function loadImage(obj,img_src,value){
    var x = isIE? new Image() : document.createElement("IMG"); // if the browser is IE, then use new Image(), otherwise, use document.createElement("IMG") to be compatible
    x.setAttribute("src",img_src); // set x's image source
    x.setAttribute("alt",value);   // set x's alt value
    x.width = 40;       //x.borderRadius = '3px'; // x.alt = alt;
    obj.value = value;  //update the cell value
    obj.appendChild(x); // append the image child to object/element
  }
// respond to confirm end setting button
  function endSetUp(){
    if (this.confirmSetting == false) {
      if (this.setRob == false) {
        alert("Error: You need to place your robot.")
      }else{
        this.ableToPlay = true;
        alert("Congratulations! You can play the game now!")
        this.canSetUp = false // unable the setUp function
        containerSelector(); // call containerSelector
        preparePlay(); // proceed to play stage
      }
      this.confirmSetting = true;
    }
  }
