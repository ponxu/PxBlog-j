<#import "lib/common.ftl" as com>

<@com.page>
<script type="text/javascript" src="${appPath}/static/kindeditor/kindeditor-min.js"></script>
<script type="text/javascript">
	/** 分类 */
	function categoryChange(val) {
		if (val == -1) {
			$("#newcategory").show();
		} else {
			$("#newcategory").hide();
		}
	}
	
	/** 新增标签 */
	function addTag() {
		var newtag = $("#newtag").val();
		var tipOnNewTag = function (tip) {
			$("#newtag").qtip({
				position: {
					my: "bottom center",
					at: "top center",
					viewport: $(window)
				},
				style: { classes: 'ui-tooltip-red ui-tooltip-shadow ui-tooltip-rounded' },
				content: { text: tip },
				show: { ready: true },
				hide: { event: "focus", inactive: 5000 },
				events: {
					hide: function(event, api) {
						$("#newtag").qtip("destroy");
					}
				}
			});
		};
		
		if ($.trim(newtag) == "") {
			tipOnNewTag("请输入标签!");
			return;
		}
		if ($.trim(newtag).replace(/\d*/, "") == "") {
			tipOnNewTag("标签不能为纯数字!");
			return;
		}
		
		// 检查是否重复
		var isExist = false;
		$("#tagul li input[type=checkbox]").each(function(i, n){
			if ($(n).val() == newtag) {
				tipOnNewTag("此标签已存在!");
				$(n).attr("checked", true);
				isExist = true;
				return false;
			}
		});
		if (isExist) return;
		
		var check = $("<input type='checkbox' name='tag' checked='checked'>").val(newtag);
		var label = $("<label>").addClass("btn").addClass("rcorner").append(check).append(newtag);
		var li = $("<li>").append(label).prependTo("#tagul");
		$("#newtag").val("");
	}
	
	$(document).ready(function() {
		var editor = KindEditor.create('textarea[name="content"]', {
			themeType: "simple",
			uploadJson: '${appPath}/admin/upload_json.jsp',
			fileManagerJson: "${appPath}/admin/file_manager_json.jsp",
			allowFileManager: true
		});
		
		var myForm = $("form:first");
		myForm.validate({
			onkeyup: false,
			onfocusout: false,
			onclick: false,
			focusInvalid: false,
			focusCleanup: false,
			rules: {
				title: { required: true },
				category: { required: true },
				newcategory: { 
					required: { 
						depends: function(element) {
							return $("#category").val() == -1; 
						} 
					}
				},
				newtag: { 
					required: { 
						depends: function(element) {
							$("#newtag").val("");
							return $("[name=tag]:checked").size() == 0; 
						} 
					}
				}
			},
			errorPlacement: function(error, element) {
				var elem = $(element);
				elem.qtip({
					content: error.text(),
					position: {
						my: "bottom left",
						at: "top center",
						viewport: $(window)
					},
					show: { event: false, ready: true },
					hide: { event: "click", inactive: 5000 },
					style: { classes: 'ui-tooltip-red ui-tooltip-shadow ui-tooltip-rounded' }
				});
			}
		});
	});
	
	function setStatus(s) {
		$("input[type=hidden][name=post_status]").val(s);;
	}
</script>

<div class="main_width" id="postdiv">
	<form action="${appPath}/admin/Post_save.do" method="post">
		<input type="hidden" name="id" value="${post.id!'0'}">
		<input type="hidden" name="post_type" value="${post.post_type!'post'}">
		<input type="hidden" name="post_status" value="${post.post_status!'draft'}">
		<input type="hidden" name="comment_status" value="${post.comment_status!'open'}">
		
		<div class="divikep">
			<div style="float: right;">
				<input type="submit" class="btn rcorner" title="保存: 只是存入草稿箱!" onclick="setStatus('draft')" value=" 保 存 ">
				<input type="submit" class="btn rcorner" title="发布: 通过博客可以看见!" onclick="setStatus('publish')" value=" 发 布 ">
			</div>
			标题:
			<select name="category" id="category" class="input rcorner" onchange="categoryChange(this.value)">
				<option value="">-请选择分类-</option>
				<#list terms as t>
					<#if t.taxonomy=='category'>
						<option value="${t.term_taxonomy_id}">${t.name}</option>
					</#if>
				</#list>
				<option value="-1">-新建分类-</option>
			</select>
			<input type="text" name="newcategory" id="newcategory" class="input rcorner" style="width: 160px; display: none;">
			<input type="text" name="title" id="title" class="input rcorner" style="width: 500px;" value="${post.post_title!'请输入文章标题!'}">
		</div>
		
		<div class="divikep">
			<textarea name="content" id="content" cols="1" rows="1" style="width: 100%; height: 450px;">${post.post_content!"在这里输入内容!"}</textarea>
		</div>
		
		<div class="divikep">
			标签:
			<input type="text" name="newtag" id="newtag" class="input rcorner" style="width: 220px;">
			<input type="button" class="btn rcorner" title="增加新标签!" value="新增" onclick="addTag()">
		</div>
		<div class="divikep">
			<ul class="tag" id="tagul">
			<#list terms as t>
				<#if t.taxonomy=='post_tag'>
					<li><label class="btn rcorner"><input type="checkbox" name="tag" value="${t.term_taxonomy_id}">${t.name}</label></li>
				</#if>
			</#list>
			</ul>
		</div>
	</form>
</div>

<script type="text/javascript">
<#if post_terms??>
	<#list post_terms as tt>
		<#if tt.taxonomy == "post_tag">
			$("[value=${tt.term_taxonomy_id}]").attr("checked", true);
		<#elseif tt.taxonomy == "category">
			$("#category").val('${tt.term_taxonomy_id}');
		</#if>
	</#list>
</#if>
</script>
</@com.page>