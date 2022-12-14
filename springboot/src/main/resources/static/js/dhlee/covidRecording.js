/**
 * 
 */
 //webkitURL is deprecated but nevertheless
URL = window.URL || window.webkitURL;

var gumStream; 						//stream from getUserMedia()
var rec; 							//Recorder.js object
var input; 							//MediaStreamAudioSourceNode we'll be recording

//blob to audiofile
var audiofile;

// shim for AudioContext when it's not avb. 
var AudioContext = window.AudioContext || window.webkitAudioContext;
var audioContext //audio context to help us record

var recordButton = document.getElementById("recordButton");
var stopButton = document.getElementById("stopButton");
var pauseButton = document.getElementById("pauseButton");

//add events to those 2 buttons
recordButton.addEventListener("click", startRecording);
stopButton.addEventListener("click", stopRecording);
pauseButton.addEventListener("click", pauseRecording);

function startRecording() {
	console.log("recordButton clicked");

	/*
		Simple constraints object, for more advanced audio features see
		https://addpipe.com/blog/audio-constraints-getusermedia/
	*/

	var constraints = { audio: true, video: false }

	/*
	  Disable the record button until we get a success or fail from getUserMedia() 
  */

	recordButton.disabled = true;
	stopButton.disabled = false;
	pauseButton.disabled = false

	/*
		We're using the standard promise based getUserMedia() 
		https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia
	*/

	navigator.mediaDevices.getUserMedia(constraints).then(function(stream) {
		console.log("getUserMedia() success, stream created, initializing Recorder.js ...");

		/*
			create an audio context after getUserMedia is called
			sampleRate might change after getUserMedia is called, like it does on macOS when recording through AirPods
			the sampleRate defaults to the one set in your OS for your playback device
		*/
		audioContext = new AudioContext();

		//update the format 
		document.getElementById("formats").innerHTML = "Format: 1 channel pcm @ " + audioContext.sampleRate / 1000 + "kHz"

		/*  assign to gumStream for later use  */
		gumStream = stream;

		/* use the stream */
		input = audioContext.createMediaStreamSource(stream);

		/* 
			Create the Recorder object and configure to record mono sound (1 channel)
			Recording 2 channels  will double the file size
		*/
		rec = new Recorder(input, { numChannels: 1 })

		//start the recording process
		rec.record()

		console.log("Recording started");

	}).catch(function(err) {
		//enable the record button if getUserMedia() fails
		recordButton.disabled = false;
		stopButton.disabled = true;
		pauseButton.disabled = true
	});
}

function pauseRecording() {
	console.log("pauseButton clicked rec.recording=", rec.recording);
	if (rec.recording) {
		//pause
		rec.stop();
		pauseButton.innerHTML = "Resume";
	} else {
		//resume
		rec.record()
		pauseButton.innerHTML = "Pause";

	}
}

function stopRecording() {
	console.log("stopButton clicked");

	//disable the stop button, enable the record too allow for new recordings
	stopButton.disabled = true;
	recordButton.disabled = false;
	pauseButton.disabled = true;

	//reset button just in case the recording is stopped while paused
	pauseButton.innerHTML = "Pause";

	//tell the recorder to stop the recording
	rec.stop();

	//stop microphone access
	gumStream.getAudioTracks()[0].stop();

	//create the wav blob and pass it on to createDownloadLink
	rec.exportWAV(createDownloadLink);
}

/* ???????????? BOX DOM */
const dropArea = document.querySelector(".drop_box"),
	button = dropArea.querySelector("button"),
	dragText = dropArea.querySelector("header"),
	inputfile = dropArea.querySelector("#fileID");
	
	
function createDownloadLink(blob) {
	
	var url = URL.createObjectURL(blob);
	var au = document.createElement('audio');
	var li = document.createElement('li');
	var link = document.createElement('a');

	//name of .wav file to use during upload and download (without extendion)
	var filename = new Date().toISOString();

	//add controls to the <audio> element
	au.controls = true;
	au.src = url;
	
	//save to disk link
	link.href = url;
	link.download = filename + ".wav"; //download forces the browser to donwload the file using the  filename
	link.innerHTML = "Save to disk";

	//add the new audio element to li
	li.appendChild(au);

	//add the filename to the li
	li.appendChild(document.createTextNode(filename + ".wav "))

	//add the save to disk link to li
	li.appendChild(link);
	
	//upload link
	var upload = document.createElement('a');
	upload.href = "#";
	upload.innerHTML = "Upload";
	upload.addEventListener("click", function(event) {
		let uploadHTML = `
	  	<audio id="audio" controls>
	  		<source src="${url}" id="src" />
		</audio>
	    <form action="COVIDUpload" method="post" enctype="multipart/form-data" class="recForm">
	    <div class="form">
	    <h4>${filename}</h4>
	    <input type="file" src="${url}" hidden accept=".wav" id="recfile" name="recfile" style="display: none;">
	    <input type="hidden" id="base64str" name="base64str" value="">
	    <input type="hidden" id="respiratory_condition" name="respiratory_condition" value="">
	    <input type="hidden" id="fever_or_muscle_pain" name="fever_or_muscle_pain" value="">
	    <input type="submit" hidden id="audiosub" value="upload">
	    </div></form>`;
	    dropArea.innerHTML = uploadHTML;
		document.getElementById("audio").load();
		
		//blob => base64??? ??????
		changesrc(blob, document.getElementById('src'));
		changesrc(blob, document.getElementById('recfile'));
	});
	li.appendChild(document.createTextNode(" "))//add a space in between
	li.appendChild(upload)//add the upload link to li

	//add the li element to the ol
	recordingsList.appendChild(li);
	
}

/* src??? blob => data64???????????? ???????????? ?????? */
function changesrc(blob, chdocument){
	var reader = new FileReader();
	reader.onload = function(e){
		chdocument.src = reader.result;
		console.log("reader??? src??????.")
	}
	reader.readAsDataURL(blob);
}

button.onclick = () => {
	inputfile.click();
};

inputfile.addEventListener("change", function(e) {
	var fileName = e.target.files[0].name;
	var files = e.target.files;
	var fPath = URL.createObjectURL(files[0]);

	let filedata = `
  	<audio id="audio" controls>
  		<source src="${fPath}" id="src" />
	</audio>
    <form action="COVIDUpload" method="post" enctype="multipart/form-data" class="recForm">
    <div class="form">
    <h4>${fileName}</h4>
    <input type="file" src="${fPath}" hidden accept=".wav" id="recfile" name="recfile" style="display: none;">
    <input type="hidden" id="base64str" name="base64str" value="">
    <input type="hidden" id="respiratory_condition" name="respiratory_condition" value="">
    <input type="hidden" id="fever_or_muscle_pain" name="fever_or_muscle_pain" value="">
    <input type="submit" hidden id="audiosub" value="upload">
    </div></form>`;
	dropArea.innerHTML = filedata;
	document.getElementById("audio").load();
	
	changesrc(files[0], document.getElementById('src'));
	changesrc(files[0], document.getElementById('recfile'));
});

const formBtn = document.querySelector("#checkbtn");
const audiosub = document.querySelector("#audiosub");
$("#checkbtn").click(function(){
	var is_respiratory = $("input[type=radio][name=is_respiratory]:checked").val();
	var is_fever = $("input[type=radio][name=is_fever]:checked").val();
	if($(".form").length){
		console.log("form is exists");
		console.log(is_respiratory+":"+is_fever);
		$("#respiratory_condition").val(is_respiratory);
		$("#fever_or_muscle_pain").val(is_fever);
		alert($('#recfile').attr('src'));
		$('#base64str').val($('#recfile').attr('src'))
		$(".recForm").submit();
	}else{
		console.log("form is exists");
		alert("??????????????? ??????????????????.");
	}
});