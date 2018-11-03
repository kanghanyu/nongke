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
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var phone = $("#phone").val();
	var orderId = $("#orderId").val();
	var status = $("#status").val();
	var data = {
			"pageNum":pageNum,"pageSize":pageSize,"startDate":startDate,"endDate":endDate,
			"phone":phone,"orderId":orderId,"status":status
	}
	
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/order/dataList",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(data != null&& null != data.page){
				$("#tbody").html("");
				var htmlStr="";
				$.each(data.page.list, function (index, order) {
					var num = order.products.length;
					var discount = order.discount!=null?order.discount:"暂无";
					var discountMoney = order.discountMoney!=null?order.discountMoney:order.totalMoney;
					var payType = order.payType==1?"点卡":(order.payType==3?"支付宝":"微信");
					var rmb =(order.rmb!=null&&order.rmb!=0)?order.rmb:"0";
					var cornMoney =(order.cornMoney!=null&&order.cornMoney!=0)?order.cornMoney:"0";
					var payTime = order.payTime != null ?timeStamp2String(order.payTime):"";
					
					htmlStr += "<tr>";
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.orderId+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.statusStr+'</td>';
					htmlStr += '<td width="2%" style="vertical-align: middle;" rowspan="'+num+'">'+timeStamp2String(order.createTime)+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.amountPhone+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.totalMoney+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.postage+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.totalPayable+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+discount+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+discountMoney+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.totalPay+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+payType+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+rmb+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+cornMoney+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+payTime+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.userName+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.phone+'</td>';
					htmlStr += '<td width="3%" style="vertical-align: middle;" rowspan="'+num+'">'+order.address+'</td>';
					$.each(order.products, function (index, product) {
						if(index ==0 ){
							htmlStr += '<td style="vertical-align: middle;" width="3%" >'+product.productName+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" ><img src="'+product.img+'" height="40px" width="40px"></td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.productPrice+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.amount+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.total+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="3%" rowspan="'+num+'">测试列</td>';
						}else{
							htmlStr += "<tr>";
							htmlStr += '<td style="vertical-align: middle;" width="3%" >'+product.productName+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" ><img src="'+product.img+'" height="40px" width="40px"></td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.productPrice+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.amount+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.total+'</td>';
							htmlStr += '</tr>';
						}
					});
					htmlStr += '</tr>';
				});
				$("#tbody").html(htmlStr);
				if(null != data.count){
					$("#countTotalMoney").text(data.count.totalMoney+"(元)");
					$("#countTotalCardMoney").text(data.count.totalCardMoney+"(元)");
					$("#countTotalCommission").text(data.count.totalCommission+"(元)");
					$("#countUserAmount").text(data.count.vipNum+"人/"+data.count.amount+"人");
				}
			}
		}
	});
}

function timeStamp2String(time){
    var datetime = new Date();
    datetime.setTime(time);
    var year = datetime.getFullYear();
    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
    var hour = datetime.getHours()< 10 ? "0" + datetime.getHours() : datetime.getHours();
    var minute = datetime.getMinutes()< 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
    var second = datetime.getSeconds()< 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
    return year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second;
}

function search(){
	var pages = 0;
	var pageNum = 1
	var pageSize = $("#pageSize").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	var phone = $("#phone").val();
	var orderId = $("#orderId").val();
	var status = $("#status").val();
	var data = {
			"pageNum":pageNum,"pageSize":pageSize,"startDate":startDate,"endDate":endDate,
			"phone":phone,"orderId":orderId,"status":status
	}
	
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/order/dataList",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(data != null && null != data.page){
				pageNum = data.page.pageNum
				pages = data.page.pages;
				$("#tbody").html("");
				var htmlStr="";
				$.each(data.page.list, function (index, order) {
					var num = order.products.length;
					var discount = order.discount!=null?order.discount:"暂无";
					var discountMoney = order.discountMoney!=null?order.discountMoney:order.totalMoney;
					var payType = order.payType==1?"点卡":(order.payType==3?"支付宝":"微信");
					var rmb =(order.rmb!=null&&order.rmb!=0)?order.rmb:"0";
					var cornMoney =(order.cornMoney!=null&&order.cornMoney!=0)?order.cornMoney:"0";
					var payTime = order.payTime != null ?timeStamp2String(order.payTime):"";
					htmlStr += "<tr>";
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.orderId+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.statusStr+'</td>';
					htmlStr += '<td width="2%" style="vertical-align: middle;" rowspan="'+num+'">'+timeStamp2String(order.createTime)+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.amountPhone+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.totalMoney+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.postage+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.totalPayable+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+discount+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+discountMoney+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.totalPay+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+payType+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+rmb+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+cornMoney+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+payTime+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.userName+'</td>';
					htmlStr += '<td width="1%" style="vertical-align: middle;" rowspan="'+num+'">'+order.phone+'</td>';
					htmlStr += '<td width="3%" style="vertical-align: middle;" rowspan="'+num+'">'+order.address+'</td>';
					$.each(order.products, function (index, product) {
						if(index ==0 ){
							htmlStr += '<td style="vertical-align: middle;" width="3%" >'+product.productName+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" ><img src="'+product.img+'" height="40px" width="40px"></td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.productPrice+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.amount+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.total+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="3%" rowspan="'+num+'">测试列</td>';
						}else{
							htmlStr += "<tr>";
							htmlStr += '<td style="vertical-align: middle;" width="3%" >'+product.productName+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" ><img src="'+product.img+'" height="40px" width="40px"></td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.productPrice+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.amount+'</td>';
							htmlStr += '<td style="vertical-align: middle;" width="1%" >'+product.total+'</td>';
							htmlStr += '</tr>';
						}
					});
					htmlStr += '</tr>';
				});
				$("#tbody").html(htmlStr);
				if(null != data.count){
					$("#countTotalMoney").text(data.count.totalMoney+"(元)");
					$("#countTotalPay").text(data.count.totalPay+"(元)");
				}
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


function setUserStatus(uid,type){
		var msg = "您真的确定要";
		var s= "";
		var data ="";
		if(type == 1){
			s = "冻结当前用户";
			data = {
		    		 "uid":uid,"status":1
		    	}
		}else if(type == 2){
			s = "升为管理员";			
			data = {
					"uid":uid,"isManager":1
			}
		}else if(type == 3){
			s = "手动添加vip";			
			data = {
					"uid":uid,"isVip":1
			}
		}else if(type == 4){
			s = "解冻当前用户";			
			data = {
					"uid":uid,"status":0
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
						window.location.href = "/user/toUserList?pageSize=10&pageNum=1";
					}
				}
			});
			return true;
		} else {
			return false;
		}
	}




