function fillReportList(page){
    document.getElementById('nowPage').innerHTML=page;
    let param = new URLSearchParams();
    param.append('page', page);
    axios({
        method: 'GET',
        params: param,
        url: '/api/admin/reports',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        //alert(3)
        let total= response.data['total'];
        let reports = response.data['reports'];
        let block=document.getElementById('reportList');
        block.innerHTML='';
        for(let i in reports){
            let id=reports[i]['id'];
            let type = reports[i]['type'];
            let sender_id=reports[i]['sender_id'];
            let target_id=reports[i]['target_id'];
            block.innerHTML+='<div id="report'+id+'">\n' +
                '               <span>'+id+'举报类型:'+type+' 发送者:'+sender_id+'被举报者:'+target_id+'</span>\n' +
                '               <input type="button" value="查看" onclick="showDetail('+id+',\''+type+'\')">\n' +
                '               <input type="button" value="忽略" onclick="ignoreReport('+id+')">\n' +
                '               <br>\n' +
                '             </div>'
        }
    })
}

function ignoreReport(id){
    let param=new URLSearchParams();
    param.append('id',id);
    axios({
        method: 'DELETE',
        url: '/api/admin/reports/'+id,
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        alert(response.data['message']);
        let block=document.getElementById('report'+id);
        block.innerHTML='';
    })
}

function showDetail(id,type){
    // alert(id)
    // alert(type)
    sessionStorage.setItem('id',id);
    sessionStorage.setItem('type',type);
    location.href='/html/search.html';
}

function prevPage(){
    let nowPage=parseInt(document.getElementById('nowPage').innerHTML);
    if(nowPage===1){
        fillReportList(1);
    }else{
        fillReportList(nowPage-1);
    }
}

function nextPage(){
    let nowPage=parseInt(document.getElementById('nowPage').innerHTML);
    fillReportList(nowPage+1);
}