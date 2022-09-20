<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- <div class="container"> -->
<link href="/mireu/css/chatbot.css?" rel="stylesheet" />

<style>
/* 전체 틀 */
body {
	margin: 0;
}

.outer {
	height: 100vh;
	overflow-y: auto;
}

.outer::-webkit-scrollbar {
	display: none;
}

.inner {
	height: 100vh;
	position: relative;
	background-color: #3478f5;
}

.innerImg {
	height: 100%;
}

.mainImg {
	width:100%;
}

/* 첫 슬라이드  */
.textMain1 {
	position: absolute;
	top: 40%;
	left: 10%;
	width: 100%;
	color: white;
}

.btnMain1 {
	width: 10%;
	height: 10%;
	margin-top: 40px;
	padding: 10px;
	color: white;
	border: solid 1px #ffffff;
	border-radius: 20px;
	background-color: #80a7ce;
}

.btnMain1:hover {
	background-color: white;
	color: #80a7ce;
	border: solid 1px #ffffff;
	border-radius: 20px;
}

/* 두번째 슬라이드 */
.btnGroupMain2 {
	position: absolute;
	top: 22%;
	left: 50%;
	transform: translate(-50%, 0%);
}

.btnMain2 {
	width: 200px;
	height: 64px;
	color: #3478f5;
	font-weight: bolder;
	border: solid 1px #3478f5;
	margin: 0 16px;
	border-radius: 20px;
	background-color: white;
}

/* 세번째 슬라이드 */
.textMain3 {
	position: absolute;
	top: 30%;
	left: 40%;
	transform: translate(-50%, 0%);
	color: white;
}

.btnMain3 {
	width: 200px;
	height: 48px;
	margin-top: 40px;
	padding: 10px;
	color: white;
	border: solid 1px #ffffff;
	border-radius: 20px;
	background-color: #3478f5;
}
</style>
	<div class="inner">
		<div class="innerImg">
			<img src="https://ifh.cc/g/RXBz7C.jpg" class="mainImg" />
		</div>
		<div class="textMain1">
			<h2>HeyDr.</h2>
			<br></br> <span> 비대면 의료 통합 웹 서비스 <br> 삶의 질을 높여드리는 건강한 서비스
				입니다.
			</span> <br></br>
			<button class="btnMain1">병원리스트 보기</button>
		</div>
	</div>
	<div class="inner">
		<div class="innerImg">
			<img src=https://ifh.cc/g/tzoLJK.jpg class="mainImg" />
		</div>
		<div class="btnGroupMain2">
			<button type="button" class="btnMain2">App Store</button>
			<button type="button" class=" btnMain2">Google Play</button>
		</div>
	</div>
	<div class="inner">
        <div class="innerImg">
          <img src="https://ifh.cc/g/zTr63k.jpg" class="mainImg" />
        </div>
        <div class="textMain3">
          <h2>HeyDr.</h2>
          <br></br>
          <span>비대면 진료 와 약 배송을 한번에.</span>
          <br></br>
            <button class="btnMain3">서비스페이지로</button>
    
        </div>
      </div>

<div id="center-text">
    <h2>ChatBox UI</h2>
    <p>Message send and scroll to bottom enabled </p>
  </div> 
<div id="body"> 
  
<div id="chat-circle" class="btn btn-raised">
        <div id="chat-overlay"></div>
        <i class="material-icons">speaker_phone</i>
  </div>
  
  <div class="chat-box">
    <div class="chat-box-header">
      ChatBot
      <span class="chat-box-toggle"><i class="material-icons">close</i></span>
    </div>
    <div class="chat-box-body">
      <div class="chat-box-overlay">   
      </div>
      <div class="chat-logs">
       
      </div><!--chat-log -->
    </div>
    <div class="chat-input">      
      <form>
        <input type="text" id="chat-input" placeholder="Send a message..."/>
      <button type="submit" class="chat-submit" id="chat-submit"><i class="material-icons">send</i></button>
      </form>      
    </div>
  </div>
 
</div>

<script>
$(function() {
	  var INDEX = 0; 
	  $("#chat-submit").click(function(e) {
	    e.preventDefault();
	    var msg = $("#chat-input").val(); 
	    if(msg.trim() == ''){
	      return false;
	    }
	    generate_message(msg, 'self');
	    var buttons = [
	        {
	          name: 'Existing User',
	          value: 'existing'
	        },
	        {
	          name: 'New User',
	          value: 'new'
	        }
	      ];
	    setTimeout(function() {      
	      generate_message(msg, 'user');  
	    }, 1000)
	    
	  })
	  
	  function generate_message(msg, type) {
	    INDEX++;
	    var str="";
	    str += "<div id='cm-msg-"+INDEX+"' class=\"chat-msg "+type+"\">";
	    str += "          <span class=\"msg-avatar\">";
	    str += "            <img src=\"https:\/\/image.crisp.im\/avatar\/operator\/196af8cc-f6ad-4ef7-afd1-c45d5231387c\/240\/?1483361727745\">";
	    str += "          <\/span>";
	    str += "          <div class=\"cm-msg-text\">";
	    str += msg;
	    str += "          <\/div>";
	    str += "        <\/div>";
	    $(".chat-logs").append(str);
	    $("#cm-msg-"+INDEX).hide().fadeIn(300);
	    if(type == 'self'){
	     $("#chat-input").val(''); 
	    }    
	    $(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight}, 1000);    
	  }  
	  
	  function generate_button_message(msg, buttons){    
	    /* Buttons should be object array 
	      [
	        {
	          name: 'Existing User',
	          value: 'existing'
	        },
	        {
	          name: 'New User',
	          value: 'new'
	        }
	      ]
	    */
	    INDEX++;
	    var btn_obj = buttons.map(function(button) {
	       return  "              <li class=\"button\"><a href=\"javascript:;\" class=\"btn btn-primary chat-btn\" chat-value=\""+button.value+"\">"+button.name+"<\/a><\/li>";
	    }).join('');
	    var str="";
	    str += "<div id='cm-msg-"+INDEX+"' class=\"chat-msg user\">";
	    str += "          <span class=\"msg-avatar\">";
	    str += "            <img src=\"https:\/\/image.crisp.im\/avatar\/operator\/196af8cc-f6ad-4ef7-afd1-c45d5231387c\/240\/?1483361727745\">";
	    str += "          <\/span>";
	    str += "          <div class=\"cm-msg-text\">";
	    str += msg;
	    str += "          <\/div>";
	    str += "          <div class=\"cm-msg-button\">";
	    str += "            <ul>";   
	    str += btn_obj;
	    str += "            <\/ul>";
	    str += "          <\/div>";
	    str += "        <\/div>";
	    $(".chat-logs").append(str);
	    $("#cm-msg-"+INDEX).hide().fadeIn(300);   
	    $(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight}, 1000);
	    $("#chat-input").attr("disabled", true);
	  }
	  
	  $(document).delegate(".chat-btn", "click", function() {
	    var value = $(this).attr("chat-value");
	    var name = $(this).html();
	    $("#chat-input").attr("disabled", false);
	    generate_message(name, 'self');
	  })
	  
	  $("#chat-circle").click(function() {    
	    $("#chat-circle").toggle('scale');
	    $(".chat-box").toggle('scale');
	  })
	  
	  $(".chat-box-toggle").click(function() {
	    $("#chat-circle").toggle('scale');
	    $(".chat-box").toggle('scale');
	  })
	  
	})


</script>