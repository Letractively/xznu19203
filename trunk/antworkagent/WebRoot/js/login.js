$(document).ready(function(){
		$('#button').click(function(){
			$.ajax({
				url:"loginAction.antwork?method=loginValidate",
				data:"username="+$('#username').val()+"&password="+$('#password').val(),
				type:"POST",
				beforeSend:function(){},
				success:function(data){
					if(data==1){
						$('#msg').html("正在登陆...");
						lock();
						setTimeout("to()",3000);
						}
					if(data==0){
						$('#msg').html("用户名或密码错误！");
						lock();
						}
					}
				});
			return false;
			});
		$('#close').click(function(){
			unlock();
			});
		});
	function lock(){
		 $(".backgroundDiv").css({"opacity":"0.5"}).fadeIn('normal');		
		 var scrollWidth = document.documentElement.clientWidth;
		 var scrollHeight = document.documentElement.clientHeight;
		 var divWidth = $(".info").width();
		 var divHeight = $(".info").height();
		 var divLeft = scrollWidth/2-divWidth/2;
		 var divTop = scrollHeight/2-divHeight;
		 $(".info").css({"position":"absolute","top":divTop,"left":divLeft}).fadeIn('normal');
		}
	function unlock(){
		$(".info").fadeOut('normal');
		$(".backgroundDiv").fadeOut('normal');
		$('#username').val("");
		$('#password').val("");
		}
	function to(){
		document.location.href="loginAction.antwork?method=login";
		}