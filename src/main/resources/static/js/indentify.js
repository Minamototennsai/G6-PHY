function fillIdentify(page){
    document.getElementById('nowPage').innerHTML=page
    let param=new URLSearchParams();
    param.append('page', page);
    axios({
        method: 'GET',
        params: param,
        url: '/api/admin/consultant-applications',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        let total=response.data['total'];
        let applications=response.data['applications'];
        let block=document.getElementById('identifyList');
        block.innerHTML='';
        for(let i in applications){
            let id=applications[i]['id'];
            let username=applications[i]['username'];
            let authentication_code=applications[i]['authentication_code'];
            block.innerHTML+='<div id="identify'+id+'">\n' +
                '               <span>用户'+username+':'+authentication_code+'</span>\n' +
                '               <input type="button" value="接受" onclick="action('+id+','+'\'approve\''+')">\n' +
                '               <input type="button" value="拒绝" onclick="action('+id+','+'\'reject\''+')">\n' +
                '             </div>'
        }
    })
}

function action(id,act){
    axios({
        method: 'POST',
        data:{id:id,action:act},
        url: '/api/admin/consultant-applications',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        console.log(response)
        alert('操作成功'+response.data['_certificated']);
        document.getElementById('identify'+id).innerHTML='';
    })
}

function prevPage(){
    let nowPage=parseInt(document.getElementById('nowPage').innerHTML);
    if(nowPage===1){
        fillIdentify(1);
    }else{
        fillIdentify(nowPage-1);
    }
}

function nextPage(){
    let nowPage=parseInt(document.getElementById('nowPage').innerHTML);
    fillIdentify(nowPage+1);
}