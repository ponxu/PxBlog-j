<div id="comment_add" class="hava_red_a_black">
	<form action="${blog.g('blogurl')}/comment_add" menthod="post">
		<input type="hidden" name="postid" value="${post.id}">
		<input type="hidden" name="pid" id="pid" value="0">
		<input type="hidden" name="type" id="type" value="${post.post_type}">
		<div class="comment_left">
			<p>
				<label for="author">昵称</label>
				<input type="input" id="author" name="author" class="input">
			</p>
			<p>
				<label for="email">邮箱</label>
				<input type="input" id="email" name="email" class="input">
			</p>
			<p>
				<label for="url">网站</label>
				<input type="input" id="url" name="url" class="input">
			</p>
		</div>
		
		<div class="comment_right">
			<p>
				<label for="ccontent">您的评论</label>
				<textarea name="ccontent" id="ccontent" cols="1" rows="1"></textarea>
			</p>
			<p>
				<input type="submit" class="btn" value=" 提 交 " id="submitBtn">
			</p>
		</div>
	</form>
	<div class="clear"></div>
</div>

<link href="${blog.g('blogurl')}/static/qtip/jquery.qtip.min.css" rel="stylesheet">
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="${blog.g('blogurl')}/static/qtip/jquery.qtip.min.js"></script>
<script type="text/javascript" src="${blog.g('blogurl')}/static/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${blog.g('blogurl')}/static/validate/messages_cn.js"></script>
<script type="text/javascript" src="${blog.g('blogurl')}/static/kindeditor/kindeditor-min.js"></script>

<script type="text/javascript">
	var editor = null;
	$(function(){
		loadComments();
		editor = KindEditor.create("#ccontent", {
			themeType: "simple",
			resizeType : 0,
			allowPreviewEmoticons : false,
			allowImageUpload : false,
			items : [
				'bold', 'italic', 'underline','fontname', 'forecolor', 'hilitecolor', '|', 
				'insertorderedlist', 'insertunorderedlist', '|', 'link', 'unlink', 'emoticons', '|',
				'removeformat','source','selectall','about']
		});
		
		$("#comment_add form").validate({
			onkeyup: false,
			onfocusout: false,
			onclick: false,
			focusInvalid: false,
			focusCleanup: false,
			rules: {
				author: "required",
				email: { required: true, email: true },
				url: { required: true, url: true }
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
					hide: { event: "click", inactive: 10000 },
					style: { classes: 'ui-tooltip-red ui-tooltip-shadow ui-tooltip-rounded' }
				});
			},
			submitHandler: function(form) {
				editor.sync();
				if (editor.isEmpty()) {
					showSubmitTip("必须填写评论内容!");
					return;
				}
				$("#submitBtn").attr("disabled", true);
				$("#submitBtn").val("正在提交");
				// 异步提交
				var url = form.action + "?random=" + Math.random();
				
				$.getJSON(url, $('#comment_add form').serialize(), function(data) {
					if (data == null || data.status != true) {
						showSubmitTip(data.msg);
					} else {
						editor.text("");
					}
					
					$("#submitBtn").val(" 提 交 ");
					$("#submitBtn").attr("disabled", false);
				});
			}
		});
	});
	
	// 在提交按钮上显示提示
	function showSubmitTip (msg) {
		$("#submitBtn").qtip({
			style: {  classes: 'ui-tooltip-shadow ui-tooltip-rounded' },
			content: {text: msg},
			position: {
				my: "left center",
				at: "right center",
				viewport: $(window)
			},
			show: { event: false, ready: true },
			hide: { event: false, inactive: 3000 }
		});
	}
	
	// 加载评论列表
	function loadComments() {
		var url = "${blog.g('blogurl')}/comment_list?postid=${post.id}";
		$.ajax({
			type: "GET",
			async: true,
			url: url,
			dataType: 'html',
			success: function(html){
				$("#comments").replaceWith(html);
			}
		});
	}
	
	function refer(li) {
		var html = "<div class='refer'>";
		html += $("#" + li + " .commenttext").html();
		html += "</div><br>";
		
		editor.html("");
		editor.html(html);
	}
</script>