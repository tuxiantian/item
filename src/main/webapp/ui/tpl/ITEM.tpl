<div class="search_wrap">
	<form id="J_formSearch" name="J_formSearch" class="formSearch">
		<table class="search_table" cellpadding="0" cellspacing="0" border="0" width="100%">
			<tbody>
				<tr>
					<td width="6%">
						<label>类别：</label>
					</td>
					<td width="25%">
						
						<input type="text" value="" name="J_category" id="J_category" class="ui-input J_qudao_code" />
						<div id="menuContent" class="menuContent" style="display:none; position: absolute;">
							<ul id="treeDemo" class="ztree" style="margin-top:0; width:160px;"></ul>
						</div>

					</td>
					<td width="9%">
						<label>渠道代码：</label>
					</td>
					<td width="25%">
						<input type="text" value="" name="J_qudao_code" id="J_qudao_code" class="ui-input J_qudao_code" />
					</td>
					<td width="9%">
						<label>查验日期：</label>
					</td>
					<td>
						<input type="text" value="" name="J_date" id="J_date" class="ui-input Wdate" 
						onfocus="WdatePicker({maxDate:'%y-%M-%d', minDate:'1949-10-01'})" />
					</td>
				</tr>
				<tr>
					<td colspan="6" class="fn-center">
						<a class="ui-button ui-button-green fn-mr-20" href="javascript:;" hidefocus="true" id="J_search">
							<i class="iconfont" title="查询/搜索"></i>
							<span>查 询</span>
						</a>
						<a class="ui-button ui-button-red " href="javascript:;" hidefocus="true" id="J_grab">
							<span>抢 单</span>
						</a>
					</td>
				</tr>
			</tbody>
		</table>
	</form>

	<!--分页table-->
	<div class="data-table" id="dataTable">
		<table class="ui-table" cellpadding="0" cellspacing="0" border="0" width="100%">
			<colgroup>
				<col width="6%">
				<col width="">
				<col width="8%">
				<col width="18%">
				<col width="11%">
				<col width="16%">
				<col width="14%">
				<col width="13%">
			</colgroup>
			<thead>
				<tr>
					<th>序号</th>
					<th>标题</th>
					<th>内容</th>
					<th>类别</th>
					<th>状态</th>
					
					<th>操作</th>
				</tr>
			</thead>
			<tbody id="dataTbody"></tbody>
		</table>

		<div class="dataTbl-bottom fn-hide fn-clear">
	        <div class="page pagination fn-left" id="Pagination">
	        	<!-- pagination -->
	        </div>
	        <div class="paginationTools fn-right">
	        	<span class="des">跳转到第</span>
	        	<input id="gotoPage" value="" type="text" class="ui-input" />
	        	<span class="des">页</span>
	        	<a class="ui-button ui-button-normal" href="javascript:;" hidefocus="true" id="">
	        		<span>确定</span>
	        	</a>
	        </div>
		</div>
	</div>
</div>

<!--分页数据容器-->
<textarea class="fn-hide" id="dataTbl_tpl">
{{#each beans}}
<tr>
	<td>{{indexAdd @index}}</td>
	<td>
		<span class="J_tooltips" title="{{title}}">{{title}}</span>
	</td>
	<td>{{detail}}</td>
	<td>{{categoryName}}</td>
	<td>{{retNewstate status}}</td>
	
	<td>
		<a href="javascript:;" hidefocus="true" class="J_showDetail" data-id="{{itemid}}"><span>查看</span></a>
		<a href="javascript:;" hidefocus="true" class="J_repairDetail" data-id="{{itemid}}"><span>修改</span></a>
	</td>
</tr>
{{/each}}
</textarea>

<!--下拉框-->
<!-- <textarea class="fn-hide" id="province_tpl">
<option value="0">全部</option>
{{#each beans}}
<option value="{{provCode}}">{{provValue}}</option>
{{/each}}
</textarea> -->