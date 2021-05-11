$(document).ready(function(){

		let ws_chat,ws_cam,ws_cam2;
		let id;	
		let caller,callee;
		
		$("#chatBtn").click(function(){
			id=$("#chatId").val();
			ws_chat=new WebSocket("wss://192.168.219.103:8090/WebSocket4");
			ws_chat.onopen=function(){				
				ws_chat.send(id);
				id="["+id+"]";
				console.log(id+"con ok");
			}
			ws_chat.onmessage=function(msg){
				console.log(msg.data);
				if(msg.data.startsWith("<select")){
					$("#map_ids").html(msg.data);
					return;
				}else if(msg.data.includes("영상 통화를 요청")){
					let yes=confirm(msg.data);
					if(yes){
						ws_cam2=new WebSocket("wss://192.168.219.103:8090/WebSocket5");
						ws_cam2.onopen=function(){	
							let callee=$("#chatId").val();
							let caller=msg.data.split("님")[0];
							ws_cam2.send(JSON.stringify({to:caller, msg:callee+"님이 영상통화를 수락하셨습니다"}));				
							console.log("영상 통화 수락");
							start();
							call();
						}
						ws_cam2.onmessage=function(msg){
							alert(msg.data);
							
						}
					}else{
						ws_chat.send("영상 통화 거절");
					}
					return;
				}else if(msg.data.includes("영상 통화 거절")){
					alert("요청하신 영상통화가 거절되었습니다.");
					return;
				}else if(msg.data.includes("영상통화를 수락")){
					console.log("요청하신 영상통화가 수락되었습니다.");
					call();
					isInitiator=true;
					//영상 통화 하는 사람들끼리만 map을 따로 만들어야 함......!!!!!!
					return;
				}
				let oldMsg=$("textarea").val();
				let message=oldMsg+"\n"+msg.data;
				$("textarea").val(message);
			}
		});
		
		$("#msgBtn").click(function(){
			let val=$("#chatMsg").val();
			ws_chat.send(id+val);
		});
	
	const startButton = document.getElementById('startButton');
	const callButton = document.getElementById('callButton');
	const hangupButton = document.getElementById('hangupButton');
	callButton.disabled = true;
	hangupButton.disabled = true;
	startButton.addEventListener('click', start);
	callButton.addEventListener('click', before_call);
	hangupButton.addEventListener('click', hangup);
	
	let startTime;
	const localVideo = document.getElementById('localVideo');
	const remoteVideo = document.getElementById('remoteVideo');
	
	localVideo.addEventListener('loadedmetadata', function() {
	  console.log(`Local video videoWidth: ${this.videoWidth}px,  videoHeight: ${this.videoHeight}px`);
	});
	
	remoteVideo.addEventListener('loadedmetadata', function() {
	  console.log(`Remote video videoWidth: ${this.videoWidth}px,  videoHeight: ${this.videoHeight}px`);
	});
	
	remoteVideo.addEventListener('resize', () => {
	  console.log(`Remote video size changed to ${remoteVideo.videoWidth}x${remoteVideo.videoHeight}`);
	  // We'll use the first onsize callback as an indication that video has started
	  // playing out.
	  if (startTime) {
	    const elapsedTime = window.performance.now() - startTime;
	    console.log('Setup time: ' + elapsedTime.toFixed(3) + 'ms');
	    startTime = null;
	  }
	});
	
	let localStream;
	let pc1;
	let pc2;
	
	const offerOptions = {
	  offerToReceiveAudio: 1,
	  offerToReceiveVideo: 1
	};
	
	function getName() {
	  return isInitiator ? 'pc1' : 'pc2';
	}
	
	
	
	async function start() {
	  console.log('Requesting local stream');
	  startButton.disabled = true;
	  try {
	    const stream = await navigator.mediaDevices.getUserMedia({audio: true, video: true});
	    console.log('Received local stream');
	    localVideo.srcObject = stream;
	    localStream = stream;
	    callButton.disabled = false;
	    
	  } catch (e) {
		  console.log(e);
	    alert(`getUserMedia() error: ${e.name}`);
	  }
	} 
	async function before_call() {
		callee=$("#calleeId").val();
		const yes=confirm(callee+"님에게 영상 통화 요청하시겠습니까?");
		if(yes){
			ws_cam=new WebSocket("wss://192.168.219.103:8090/WebSocket5");
			ws_cam.onopen=function(){	
				caller=$("#chatId").val();
				ws_cam.send(JSON.stringify({caller,callee,msg:caller+"님이 영상 통화를 요청하셨습니다",to:callee}));				
				console.log("WebSocket5 ok");
			}
			ws_cam.onmessage=function(msg){
				console.log(msg.data);
				
			}
		}
		
	}
	
	let isInitiator=false;
	async function call() {
	  callButton.disabled = true;
	  hangupButton.disabled = false;
	  console.log('Starting call');
	  startTime = window.performance.now();
	  setTimeout(async()=>{ 
		  if(isInitiator){
			  maybeStrart();
		  }
	  }, 3000);	  
	}
	
	let isStarted=false;
	function maybeStart(){
		if(!isStarted && typeof localStream !== 'undefined' ){
			console.log('>>>>>>>> creating peer connection');
			createPeerConnection();
			pc.addStream(localStream);
			isStarted=true;
			if(isInitiator){
				doCall();
			}
		}
	}
	
	const pcConfig={'iceServers':[{url:'stun:stun.l.google.com:19302'}]};
	
	function createPeerConnection(){
		try{
			pc=new RTCPeerConnection(pcConfig);
			pc.onicecandidate=handleIceCandidate;
			pc.onaddstream=handleRemmoteAdded;
			pc.onremovestream=handleRemoteStreamRemoved;
			
		}catch(e){
			console.log(e);
			return;
		}
	}
	
	function doCall(){
		pc.createOffer(onCreateOfferSuccess,onCreateOfferError);
	}
	
	function onCreateOfferError(error) {
	  console.log(`Failed to create session description: ${error.toString()}`);
	}
	
	async function onCreateOfferSuccess(desc) {
	  console.log(`Offer from pc1\n${desc.sdp}`);
	  console.log('pc1 onCreateOfferSuccess start');
	  try {
	    await pc1.setLocalDescription(desc);
	    onSetLocalSuccess(pc1);
	  } catch (e) {
	    onSetSessionDescriptionError();
	  }
	
	  console.log('pc2 setRemoteDescription start');
	  try {
	    await pc2.setRemoteDescription(desc);
	    onSetRemoteSuccess(pc2);
	  } catch (e) {
	    onSetSessionDescriptionError();
	  }
	
	  console.log('pc2 createAnswer start');
	  // Since the 'remote' side has no media stream we need
	  // to pass in the right constraints in order for it to
	  // accept the incoming offer of audio and video.
	  try {
	    const answer = await pc2.createAnswer();
	    await onCreateAnswerSuccess(answer);
	  } catch (e) {
	    onCreateSessionDescriptionError(e);
	  }
	}
	
	function onSetLocalSuccess(pc) {
	  console.log(`${getName(pc)} setLocalDescription complete`);
	}
	
	function onSetRemoteSuccess(pc) {
	  console.log(`${getName(pc)} setRemoteDescription complete`);
	}
	
	function onSetSessionDescriptionError(error) {
	  console.log(`Failed to set session description: ${error.toString()}`);
	}
	
	function gotRemoteStream(e) {
	  if (remoteVideo.srcObject !== e.streams[0]) {
	    remoteVideo.srcObject = e.streams[0];
	    console.log('pc2 received remote stream');
	  }
	}
	
	async function onCreateAnswerSuccess(desc) {
	  console.log(`Answer from pc2:\n${desc.sdp}`);
	  console.log('pc2 setLocalDescription start');
	  try {
	    await pc2.setLocalDescription(desc);
	    onSetLocalSuccess(pc2);
	  } catch (e) {
	    onSetSessionDescriptionError(e);
	  }
	  console.log('pc1 setRemoteDescription start');
	  try {
	    await pc1.setRemoteDescription(desc);
	    onSetRemoteSuccess(pc1);
	  } catch (e) {
	    onSetSessionDescriptionError(e);
	  }
	}
	
	async function onIceCandidate(pc, event) {
	  try {
	    await (getOtherPc(pc).addIceCandidate(event.candidate));
	    onAddIceCandidateSuccess(pc);
	  } catch (e) {
	    onAddIceCandidateError(pc, e);
	  }
	  console.log(`${getName(pc)} ICE candidate:\n${event.candidate ? event.candidate.candidate : '(null)'}`);
	}
	
	function onAddIceCandidateSuccess(pc) {
	  console.log(`${getName(pc)} addIceCandidate success`);
	}
	
	function onAddIceCandidateError(pc, error) {
	  console.log(`${getName(pc)} failed to add ICE Candidate: ${error.toString()}`);
	}
	
	function onIceStateChange(pc, event) {
	  if (pc) {
	    console.log(`${getName(pc)} ICE state: ${pc.iceConnectionState}`);
	    console.log('ICE state change event: ', event);
	  }
	}
	
	function hangup() {
	  console.log('화상 연결을 끊습니다');
	  pc1.close();
	  pc2.close();
	  pc1 = null;
	  pc2 = null;
	  hangupButton.disabled = true;
	  callButton.disabled = false;
	}
	
});		