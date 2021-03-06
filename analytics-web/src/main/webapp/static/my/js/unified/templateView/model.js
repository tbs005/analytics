define(function(require, exports, module) {
	var model = Backbone.Model.extend({
		initialize: function() {
			// do nothing
		},
		getReportTemplateById: function(id, callback) {
			$.post($$ctx + "/unified/get/" + id, function(obj) {
				callback(obj);
			});
		},
		getTaskActions: function(nodeId, callback) {
			$.post($$ctx + "/unified/getTaskActions/" + nodeId, function(r) {
				callback(r);
			});
		},
		fetchCommentDetail : function(data,callback){
			$.post($$ctx + "/unified/detail",data).success(function(r) {
				callback(r);
			}).error(function(){bootbox.alert("查看详细失败.请稍后再试");});
		},
		auditNext: function(data, callback) {
			$.post($$ctx + "/unified/auditNext",data).success(function(r) {
				callback(r);
			}).error(function(){bootbox.alert("提交失败请稍后重试");});
		},
		download: function(fileName){
			$.get($$ctx+"/unified/fileIsExists?fileName=" + fileName,function(r_data){
				if (r_data == "") {
					window.location.href = $$ctx+"/unified/download?fileName="+fileName;
				} else {
					alert(r_data);
				}
			});
		}
	});
	module.exports = model;
});