$(function(){
	var pages = $("#pages").val();
	var pageNum = $("#pageNum").val();
	var pageSize = $("#pageSize").val();
	var options={
            bootstrapMajorVersion:1,    //版本
            currentPage:pageNum,    //当前页数
            numberOfPages:5,    //最多显示Page页
            totalPages:pages,    //所有数据可以显示的页数
            onPageClicked:function(e,originalEvent,type,page){
            	addHtml(page);
            }
        }
        $("#page").bootstrapPaginator(options);
})


function addHtml(pageNum){
	var pageSize = $("#pageSize").val();
	var phone = $("#phone").val();
	var inviterPhone = $("#inviterPhone").val();
	var isManager = $("#isManager").val();
	var isVip = $("#isVip").val();
	var data = {
			"pageNum":pageNum,"pageSize":pageSize,"phone":phone,"inviterPhone":inviterPhone,"isManager":isManager,"isVip":isVip
	}
	
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/user/dataList",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(data != null){
				$("#tbody").html("");
				var htmlStr="";
				$.each(data.list, function (index, item) {
					var moneyStr = item.moneyStr!=null?item.moneyStr:"";
					var cardMoney =item.cardMoney!=null?item.cardMoney:0
							var commission =item.commission!=null?item.commission:0
									var img = item.img!=null?item.img:"";
					var inviterUid = item.inviterUid!=null?item.inviterUid:"无";
					var inviterPhone = item.inviterPhone!=null?item.inviterPhone:"无";
					var isManager = item.isManager==0?"普通用户":"管理员";
					var isVip = item.isVip==0?"普通用户":"VIP用户";
					htmlStr += "<tr>";
					htmlStr += '<td width="5%">'+item.id+'</td>';
					htmlStr += '<td width="8%">'+item.uid+'</td>';
					htmlStr += '<td width="9%">'+item.phone+'</td>';
					htmlStr += '<td width="5%">'+moneyStr+'</td>';
					htmlStr += '<td width="5%">'+cardMoney+'</td>';
					htmlStr += '<td width="5%">'+commission+'</td>';
					htmlStr += '<td width="10%"><img src="'+img+'" height="50px" width="50px"></td>';
					htmlStr += '<td width="8%">'+inviterUid+'</td>';
					htmlStr += '<td width="9%">'+inviterPhone+'</td>';
					htmlStr += '<td width="7%">'+isManager+'</td>';
					htmlStr += '<td width="7%">'+isVip+'</td>';
					htmlStr += '<td width="30%">';
					htmlStr += '<button class="btn btn-primary btn-sm" onclick="detailUserInfo('+item.uid+')">'
					htmlStr += '<i class="glyphicon glyphicon-edit">详情</i>'
					htmlStr += '</button>'
					htmlStr += '<button class="btn btn-danger btn-sm" onclick="setUserStatus('+item.id+',1)" type="button">'
					htmlStr += '<i class="fa fa-trash-o">冻结</i>'
					htmlStr += '</button>'
					htmlStr += '<button class="btn btn-info btn-sm" onclick="setUserStatus('+item.id+',2)">升为管理员</button>'
					htmlStr += '<button class="btn btn-warning btn-sm" onclick="setUserStatus('+item.id+',3)">手动添加vip</button>'
					htmlStr += '</td>'
					htmlStr += '</tr>';
				});
				$("#tbody").html(htmlStr);
			}
		}
	});
}

function search(){
	var pages = 0;
	var pageNum = 1
	var pageSize = $("#pageSize").val();
	var phone = $("#phone").val();
	var inviterPhone = $("#inviterPhone").val();
	var isManager = $("#isManager").val();
	var isVip = $("#isVip").val();
	var data = {
			"pageNum":pageNum,"pageSize":pageSize,"phone":phone,"inviterPhone":inviterPhone,"isManager":isManager,"isVip":isVip
	}
	
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/user/dataList",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(data != null){
				pageNum = data.pageNum
				pages = data.pages;
				$("#tbody").html("");
				var htmlStr="";
				$.each(data.list, function (index, item) {
					var moneyStr = item.moneyStr!=null?item.moneyStr:"";
					var cardMoney =item.cardMoney!=null?item.cardMoney:0
							var commission =item.commission!=null?item.commission:0
									var img = item.img!=null?item.img:"";
					var inviterUid = item.inviterUid!=null?item.inviterUid:"无";
					var inviterPhone = item.inviterPhone!=null?item.inviterPhone:"无";
					var isManager = item.isManager==0?"普通用户":"管理员";
					var isVip = item.isVip==0?"普通用户":"VIP用户";
					htmlStr += "<tr>";
					htmlStr += '<td width="5%">'+item.id+'</td>';
					htmlStr += '<td width="8%">'+item.uid+'</td>';
					htmlStr += '<td width="9%">'+item.phone+'</td>';
					htmlStr += '<td width="5%">'+moneyStr+'</td>';
					htmlStr += '<td width="5%">'+cardMoney+'</td>';
					htmlStr += '<td width="5%">'+commission+'</td>';
					htmlStr += '<td width="10%"><img src="'+img+'" height="50px" width="50px"></td>';
					htmlStr += '<td width="8%">'+inviterUid+'</td>';
					htmlStr += '<td width="9%">'+inviterPhone+'</td>';
					htmlStr += '<td width="7%">'+isManager+'</td>';
					htmlStr += '<td width="7%">'+isVip+'</td>';
					htmlStr += '<td width="30%">';
					htmlStr += '<button class="btn btn-primary btn-sm" onclick="detailUserInfo('+item.uid+')">'
					htmlStr += '<i class="glyphicon glyphicon-edit">详情</i>'
					htmlStr += '</button>'
					htmlStr += '<button class="btn btn-danger btn-sm" onclick="setUserStatus('+item.id+',1)" type="button">'
					htmlStr += '<i class="fa fa-trash-o">冻结</i>'
					htmlStr += '</button>'
					htmlStr += '<button class="btn btn-info btn-sm" onclick="setUserStatus('+item.id+',2)">升为管理员</button>'
					htmlStr += '<button class="btn btn-warning btn-sm" onclick="setUserStatus('+item.id+',3)">手动添加vip</button>'
					htmlStr += '</td>'
					htmlStr += '</tr>';
				});
				$("#tbody").html(htmlStr);
			}
			var options={
		            bootstrapMajorVersion:1,    //版本
		            currentPage:pageNum,    //当前页数
		            numberOfPages:5,    //最多显示Page页
			        totalPages:pages,    //所有数据可以显示的页数
		            onPageClicked:function(e,originalEvent,type,page){
		            	addHtml(page);
		            }
		        }
			if(pages >0){
				$("#page").bootstrapPaginator(options);
				$("#page").show();
			}else{
				$("#page").hide();
			}
		}
	});
}


function setUserStatus(id,type){
		var msg = "您真的确定要";
		var s= "";
		var data ="";
		if(type == 1){
			s = "冻结";
			data = {
		    		 "id":id,"status":1
		    	}
		}else if(type == 2){
			s = "升为管理员";			
			data = {
					"id":id,"isManager":1
			}
		}else if(type == 3){
			msg = "手动添加vip";			
			data = {
					"id":id,"isVip":1
			}
		}
		
		msg = msg+s+"吗？\n\n请确认！";
		if (confirm(msg) == true) {
			$.ajax({
				type : "post",
				data : JSON.stringify(data),
				url : "/user/setUserStatus",
				dataType : "json",
				contentType : 'application/json',
				success : function(data) {
					if(null != data && data.code == 1000){
						alert(s+data.msg);
					}
				}
			});
			return true;
		} else {
			return false;
		}
	}


function detailUserInfo(uid){
	var data = {
			"uid":uid
	}
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/user/getUserInfo",
		dataType : "json",
			contentType : 'application/json',
		success : function(data) {
			if(null != data && data.code == 1000){
				$('#detailModal').modal('toggle');
				var user = data.user;
				if(null != user){
					$("#uidD").val(user.uid);
					$("#phoneD").val(user.phone);
					$("#inviterUidD").val(user.inviterUid);
					$("#inviterPhoneD").val(user.inviterPhone);
					$("#moneyD").val(user.money);
					$("#cardMoneyD").val(user.cardMoney);
					$("#commissionD").val(user.commission);
					$("#imgUrlD").val(user.imgUrl);
					if(null != user.isManager){
						$("#isManagerD").val(user.isManager==0?"普通用户":"管理员");
					}
					if(null != user.isVip){
						$("#isVipD").val(user.isVip==0?"普通用户":"VIP用户");
					}
					if(null != user.img){
						$("#imgD").attr("src",user.img);
					}
				}
				
				var bank = data.bank;
				if(null != bank){
					$("#bankNameD").val(bank.bankName);
					$("#bankNumD").val(bank.bankNum);
					$("#userNameD").val(bank.userName);
					$("#phoneD1").val(bank.phone);
					$("#bankAdressD").val(bank.bankAdress);
				}
				var address = data.address;
				if(null != address){
					$("#userNameDD").val(address.userName);
					$("#phoneDD").val(address.phone);
					$("#postCodeDD").val(address.postCode);
					$("#addressDD").val(address.address);
				}
			}
		}
	});
}