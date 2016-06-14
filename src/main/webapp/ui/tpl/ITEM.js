define(['text!../tpl/ITEM.tpl'], function(tpl){
	var fnInit = function(){

		//主页面的查询table
		srvMap.add("ITEM", "tableData.json?1=1", "front/sh/item!execute?uid=getItemMagerByCdt");
		srvMap.add("SITEM", "tableData.json?1=1", "front/sh/item!execute?uid=queryItemByKey");
		srvMap.add("CATEGORY", "tableData.json?1=1", "front/sh/itemCategory!execute?uid=queryItemCategory");

		//添加模板到页面
		$("#J_wrap").html(tpl);
		/*start 类别树*/
		$("#J_category").focus(function(){
			var setting = {
				view: {
					dblClickExpand: false
				},
				
				treeNode:{
					name:"categoryName",
					open:true
				},
				data: {
					key:{
						name:"categoryName",
						title:"categoryName"
					},
					simpleData: {
						enable: true,
						idKey:"id",
						pIdKey:"pid",
						rootPId:"0"
					}
				},
				callback: {
					beforeClick: beforeClick,
					onClick: onClick
				}
			};
			var zNodes;
			$.PostJson(srvMap.get("CATEGORY"), "", function(state, json){
				if(state == 'success'){
					if(json && json.returnCode == '0'){
						zNodes=json.beans;
						$.fn.zTree.init($("#treeDemo"), setting, zNodes);
						showMenu();
					}
				}
			});

		});
		function beforeClick(treeId, treeNode) {
			var check = (treeNode && !treeNode.isParent);
			if (!check) alert("只能选择子类别...");
			return check;
		}
	
		function onClick(e, treeId, treeNode) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
			nodes = zTree.getSelectedNodes(),
			v = "";
			nodes.sort(function compare(a,b){return a.id-b.id;});
			for (var i=0, l=nodes.length; i<l; i++) {
				v += nodes[i].categoryName + ",";
			}
			if (v.length > 0 ) v = v.substring(0, v.length-1);
			var cityObj = $("#J_category");
			cityObj.attr("value", v);
		}		
		function showMenu() {
			var cityObj = $("#J_category");
			var cityOffset = $("#J_category").offset();
			$("#menuContent").css({left:cityOffset.left + "px", top:cityOffset.top-30 + "px"}).slideDown("fast");

			$("body").bind("mousedown", onBodyDown);
		}
		function hideMenu() {
			$("#menuContent").fadeOut("fast");
			$("body").unbind("mousedown", onBodyDown);
		}
		function onBodyDown(event) {
			if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
				hideMenu();
			}
		}
		/*类别树 end*/
		$("#J_search").click(function(){
			var str = $("#J_formSearch").formSerialize();
			var pageParam = {
				url: srvMap.get("ITEM"),
				formStr: str
			}
			//分页查询
			getPage(pageParam, function(json){
				$(".J_showDetail").unbind("click").bind("click",function(){

					var cId = $(this).data("id");
					var arg;
					var D_param = {
						id: "D_showYBGD",
						title: "查看工单",
						content: tpl2,
						width: 970
					}
					openWindowDiv(D_param, function(e){
						//绑定关闭弹窗事件
						dialog.get("D_showYBGD").addEventListener('close', function(){
							//$("#J_navigation").hide();
							//$("#J_search").trigger("click");
						})

						//初始化弹窗内容
						arg=null;
						arg = str + "&type=1";
						initNavi(cId,arg);
					});
				});
			});
		});
			//type=1向后查询，type=2向前查询
		function initNavi(cId,arg){
			//定义变量存储当前索引
			var cIndex = 0;
			arg += "&cId="+cId;
			$.PostJson(srvMap.get("SITEM"), "itemId="+cId, function(state, json){
				if(state == 'success'){
					if(json && json.returnCode == '0'){				
						$(".detail_wrap").temp($("#T_D_YBGD"), json.bean);						
						dialog.get("D_showYBGD").reset();
						cIndex++;

					}else{
						result_prompt(json.returnMessage || "查询失败！");
					}
				}else{
					result_prompt(json.returnMessage || "系统错误！");
				}

				//显示导航
				$("#J_navigation").show();
			})
		}
		//0.5秒后自动执行查询
		setTimeout(function(){
			$("#J_search").click();
		}, 500);
		Handlebars.registerHelper('retNewstate', function(state){
			if(state=="0"){
				return "正常";
			}else{
				return "屏蔽";
			}
			
		});

	}
	
	return fnInit;
})