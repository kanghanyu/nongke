/*
 * 全局公共变量
 */
//var network = "http://192.168.1.129:8080/"; /*内网*/
var network = "http://192.168.1.133:8082/"; /*内网*/

/*--------------login.html > start------------------- */
var user = document.getElementById("user");
var pwd = document.getElementById("pwd");

/*--------------index.html > start------------------- */
var data = JSON.parse(sessionStorage.getItem("msg"));

//创建分页
var pageNum = 10; //分页数
function createPage(currentPage, totalPages){
    var options = {
        bootstrapMajorVersion:5,
        currentPage: currentPage,
        numberOfPages: 5,
        totalPages:totalPages,
        onPageClicked: function(e,originalEvent,type,page){
            toPage(page);
        }
    }
    $('#bp-3-element-test').bootstrapPaginator(options);
}

/*----------------系统状态码------------------*/
var errorVar = [
    {"0":"请求成功"},
    {"-10000":"系统错误"},
    {"-20000":"参数不正确"},
    {"-30000":"用户未登陆"},
    {"-40000":"权限不足"},
    //操作用户错误码
    {"-10001":"用户名不能为空"},
    {"-10002":"用户不存在"},
    {"-10003":"密码不能为空"},
    {"-10004":"密码不正确"},
    {"-10005":"该手机号已注册"},
    {"-10006":"该昵称已存在"},
    {"-10007":"该手机号不存在"},
    {"-10008":"短信验证码错误或无效"},
    {"-10009":"该用户已经为普通管理员"},
    {"-10010":"该用户为超级管理员不能修改其权限"},
    {"-10011":"该用为普通用户"},
    {"-10012":"设置管理员时话题分类不能为空"},
    {"-10013":"该用户已经为正常用户不需要解封"},
    {"-10014":"该用户已经封停，不可再次封停"},
    {"-10015":"封停时间应当大于当前时间"},
    //操作话题错误码
    {"-10101":"该话题已经删除无需修改内容"},
    {"-10102":"话题不存在"},
    //操作话题分类错误码
    {"-10201":"权限不足，该用户没有该话题分类的操作权限"},
    {"-10202":"该话题分类名已存在,请重新创建"},
    //操作举报信息错误码
    {"-10301":"无法查询到举报信息"},
    {"-10302":"该举报已经处理过，无需重复处理"},
    //操作评论、回复错误码
    {"-10401":"该评论不存在、或找不到"},
    {"-10402":"该评论已经删除无需重复操作"},
    {"-10403":"该回复不存在、或找不到"},
    {"-10404":"该回复已经删除无需重复操作"}

];
/*遍历数组*/
var myError = $.map(errorVar,function(ele){
    return ele;
});

function alertWarning(msg){
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "progressBar": true,
        "preventDuplicates": false,
        "positionClass": "toast-top-center",
        "onclick": null,
        "showDuration": "400",
        "hideDuration": "1000",
        "timeOut": "1000",
        "extendedTimeOut": "500",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
    toastr.warning(msg, "警告");
}

function alertInfo(msg) {
	toastr.options = {
        "closeButton": true,//是否显示关闭按钮
        "debug": false,     //是否使用debug模式
        "progressBar": true,
        "preventDuplicates": false,
        "positionClass": "toast-top-center", //弹出窗的位置
        "onclick": null,
        "showDuration": "400", //显示的动画时间
        "hideDuration": "1000",//消失的动画时间
        "timeOut": "700",        //展现时间
        "extendedTimeOut": "1000",//加长展示时间
        "showEasing": "swing",//显示时的动画缓冲方式
        "hideEasing": "linear",//消失时的动画缓冲方式
        "showMethod": "fadeIn",//显示时的动画方式
        "hideMethod": "fadeOut"//消失时的动画方式
    };
    toastr.info(msg);
};

function alertDanger(msg) {
    toastr.options = {
        "closeButton": true,//是否显示关闭按钮
        "debug": false,     //是否使用debug模式
        "progressBar": true,
        "preventDuplicates": false,
        "positionClass": "toast-top-center", //弹出窗的位置
        "onclick": null,
        "showDuration": "400", //显示的动画时间
        "hideDuration": "1000",//消失的动画时间
        "timeOut": "700",        //展现时间
        "extendedTimeOut": "1000",//加长展示时间
        "showEasing": "swing",//显示时的动画缓冲方式
        "hideEasing": "linear",//消失时的动画缓冲方式
        "showMethod": "fadeIn",//显示时的动画方式
        "hideMethod": "fadeOut"//消失时的动画方式
    };
    toastr.error(msg);
};
function clearText(field){
    if (field.defaultValue == field.value){
        field.value = '';
    }else if(field.value == ''){
        field.value = field.defaultValue;
    }
}