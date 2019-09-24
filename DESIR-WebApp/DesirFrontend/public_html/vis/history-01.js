if (window.location.hash !== "#1"||"#2"||"#3"||"#4") {
	var noOfButtons = 3;
     var pick = Math.floor(Math.random() * noOfButtons) + 1;
     var radioBtn = document.getElementById('links' + pick);
     radioBtn.checked = true;
     var pushHistory = "#"+pick;
     history.pushState({}, '', pushHistory);
}
else{
	var pick = window.location.hash.substr(1);
	var radioBtn = document.getElementById('links' + pick);
    radioBtn.checked = true;
}

