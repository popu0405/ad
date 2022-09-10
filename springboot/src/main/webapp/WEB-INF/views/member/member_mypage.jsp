<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 필요한것 -->
<script type="text/javascript"
	src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript"
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=70d0af4a9fb4dc2835eb629734419955"></script>

<!-- bootstrap5 dataTables js cdn -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.2/css/all.min.css">
<link rel="stylesheet"
	href="//cdn.datatables.net/1.10.24/css/jquery.dataTables.min.css">

<div class="container">
	<h1>MyPage</h1>
	<table border="1" class="table table-striped">
		<thead>
			<tr>
				<th>ID</th>
				<th>NAME</th>
				<th>SSN</th>
				<th>AGE</th>
				<th>TEL</th>
				<th>EMAIL</th>
				<th>PROFIMG</th>
				<th>MDATE</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>${member.id }</td>
				<td>${member.name }</td>
				<td>${member.ssn }</td>
				<td>${member.age }</td>
				<td>${member.tel }</td>
				<td>${member.email }</td>
				<td><img src="${pageContext.request.contextPath }/upload/${member.profimg }" style="width:100px;height:70px;"></td>
				<td>${member.mdate }</td>

			</tr>

		</tbody>
	</table>
	<div class="container">
		<div id="map" style="width: 100%; height: 350px;"></div>
		
		<ul>
			<li>병원 이름:<span id="name"></span></li>
			<li>병원 주소:<span id="loc"></span></li>
			<li>영업 시간:<span id="time"></span></li>
			<li>병원 구분:<span id="cate"></span></li>
			<li>홈페이지 주소:<span id="url"></span></li>
		</ul>
		<input id="btnStop" type="button" value="감시를 끝낸다" />
	</div>

	<div class="container">
		<table class="table1 table table-striped" id="datatables">
			<thead>
				<tr>
					<th scope="col">병원이름</th>
					<th scope="col">병원주소</th>
					<th scope="col">진료구분</th>
					<th scope="col">전화번호</th>
				</tr>
			</thead>
			<tbody>

			</tbody>
		</table>
	</div>
	<div>
		<input type="button" value="수정하기"
			onclick="location.href='updateMypageForm'"> <input
			type="button" value="뒤로가기"
			onclick="location.href='${pageContext.request.contextPath}/member'">
	</div>

</div>

<script>

	$(function() { //상시 동작 함!
			<!-- 마이페이지 리스트 불러오기  -->
			$.ajax({
                url: 'http://14.36.188.14:9000/map/hospiter_list',
                type: 'GET',
                dataType: 'jsonp',
                jasonp: 'callback',
                success: function(data){
                    //console.log(data);
                    //console.log(data.columns);
                    //console.log(data.data);
                    let tbodyData =[];
                   
                    for (var i of data.data[0]) {
                    	tbodyData.push('<tr><td  id="listBtn" style="cursor:pointer;">'+i.hos_name+'</td><td>'+i.hos_address+'</td><td>'+i.hos_loc+'</td><td>'+i.hos_tel+'</td></tr>')
                    }
                    document.querySelector('.table1 > tbody').innerHTML = tbodyData.join('');
                },
                error: function(err){
                   console.log('Error => '+$('#target').text());
                }
            });
			//-----------------------------------------------
			// 클릭 시
			$(".table1").on("click","#listBtn",function(){
				var hos_value=$(this).text();
				//console.log('hos_value => '+hos_value);
				//console.log('td_text => '+$(this).text());
				$.ajax({
	                url: 'http://14.36.188.14:9000/map/detail?name='+hos_value+'&',
	                type: 'GET',
	                dataType: 'jsonp',
	                jasonp: 'callback',
	                contentType : "application/jsonp; charset: UTF-8",
	                success: function(data){
	                    //console.log('data.columns= '+data.columns);
	                    //console.log('data.columns= '+data.columns[1]);
	                    //console.log('data.data= '+data.data[0]);
	                    //console.log('data.data= '+data.data[0][2]);
	                    //console.log('data.data.length= '+data.data[0].length);
	                    var test= {}
	                    var hos_marker = {}
	                    
	                    for(var i =0; i < data.data[0].length; i++){
	                    	hos_marker[data.columns[i]]=data.data[0][i];
	                    }
	                    console.log('결과1= '+hos_marker['hos_x']);
	                    console.log('결과2= '+hos_marker['hos_y']);
	                    console.log('병원이름= '+hos_marker['hos_name']);
	                    
	                    <!-- 지도 생성! -->
	            		var mapContainer = document.getElementById('map'), // 지도를 표시할 div 
	            		mapOption = {
	            			center : new kakao.maps.LatLng(33.450701, 126.570667), // 지도의 중심좌표
	            			level : 5
	            		// 지도의 확대 레벨 
	            		};
	            		var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
	            		<!-- 지도 크기 조절 이벤트 -->
	            		var zoomControl = new kakao.maps.ZoomControl();
	            		map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
	            		
	            		<!-- 현재위치 좌표얻어오기! -->
            			navigator.geolocation.getCurrentPosition(function(position) {

           				var lat = position.coords.latitude, // 위도
           				lon = position.coords.longitude; // 경도

           				var locPosition = new kakao.maps.LatLng(lat, lon), // 마커가 표시될 위치를 geolocation으로 얻어온 좌표로 생성합니다
           				message = '<div style="padding:5px;">여기에 계신가요?!</div>'; // 인포윈도우에 표시될 내용입니다
           				console.log('현재위치 !!'+locPosition);
	            		var positions = [
	            			{
	            		        title: '현재위치', 
	            		        latlng: locPosition
	            		    },
	            			{
	            		        title: hos_marker['hos_name'], 
	            		        latlng: new kakao.maps.LatLng(hos_marker['hos_y'], hos_marker['hos_x'])
	            		    }];
	            		
	            		console.log('lat = '+lat)
	            		console.log('marker_y = '+hos_marker['hos_y'])
	            		console.log('y = '+(lat+hos_marker['hos_y'])/2)
	            		console.log('---------------')
	            		console.log('lat = '+lon)
	            		console.log('marker_y = '+hos_marker['hos_x'])
	            		console.log('x = '+(lon+hos_marker['hos_x'])/2)
	            		var center_x = (lon+hos_marker['hos_x'])/2
	            		var center_y = (lat+hos_marker['hos_y'])/2
	            		var center_xy= new kakao.maps.LatLng(center_y,center_x)
	            		for (var i = 0; i < positions.length; i ++) {
	            		// 마커를 생성합니다 
	        			var marker = new kakao.maps.Marker({
	        				map : map,
	        				position : positions[i].latlng,
	        				title : positions[i].title
	        			});
	            		
	            		var middle = Math.abs(positions[0]-positions[1])
	            		//console.log(typeof(positions)); object
	            		//console.log(positions.length); 2
	            		console.log("latlng"+[i]+'= '+positions[i].latlng);
	            		console.log("title"+[i]+'= '+positions[i].title);
	            		// 두 좌표 평균값
	            		console.log('평균 구하기 step1 = '+ positions[0].latlng);
	        			var iwContent = positions[i].title, // 인포윈도우에 표시할 내용
	        			iwRemoveable = true;
	        			// 인포윈도우를 생성합니다
	        			var infowindow = new kakao.maps.InfoWindow({
	        				content : iwContent,
	        				removable : iwRemoveable
	        			});
	        			// 인포윈도우를 마커위에 표시합니다 
	        			infowindow.open(map, marker);
	        			// 지도 중심좌표를 접속위치로 변경합니다
	        			map.setCenter(center_xy);
	        			// 마커를 지도에 표시합니다
	        			marker.setMap(map);
	            		}
	            		});
	            		
	                } //ajax - success 함수 
				
				}); // ajax 끝
			}); //병원이름 클릭 함수
			
			
		});
	</script>
