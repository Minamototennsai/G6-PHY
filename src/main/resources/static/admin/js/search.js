function search(id,type){
    let param=new URLSearchParams();
    param.append("id",id);
    let display=document.getElementById('display');
    display.innerHTML='';
    if(type==='user'){
        axios({
            method:"get",
            url:'/api/admin/search/user',
            params:param,
            headers:{
                Authorization: 'Bearer '+sessionStorage.getItem('token')
            }
        }).then(function (response){
            let id=response.data['id'];
            let name=response.data['name'];
            display.innerHTML=' <div>\n' +
                '                   <span>'+id+'用户:'+name+'</span><span id="banning"></span>\n' +
                '               </div>\n' +
                '               <div>\n' +
                '                   <input type="button" value="封禁" onclick="banUser('+id+')">\n' +
                '                   <input type="button" value="解封" onclick="unbanUser('+id+')">\n' +
                '               </div>'
        })
    }else if(type==='consult'){
        axios({
            method:"get",
            url:'/api/admin/search/session',
            params:param,
            headers:{
                Authorization: 'Bearer '+sessionStorage.getItem('token')
            }
        }).then(function (response){
            let logs=response.data['logs'];
            for(let i in logs){
                display.innerHTML+='<div>\n' +
                    '                    <span>'+logs[i]['id']+':name='+logs[i]['name']+':'+logs[i]['content']+'</span>\n' +
                    '               </div>'
            }
            display.innerHTML+='<div>\n' +
                '                   <input type="button" value="立刻结束" onclick="closeSession('+id+')">\n' +
                '               </div>'
        })
    }else if(type==='article'){
        axios({
            method:"get",
            url:'/api/admin/search/article',
            params:param,
            headers:{
                Authorization: 'Bearer '+sessionStorage.getItem('token')
            }
        }).then(function (response){
            let user_id=response.data['user_id'];
            let name=response.data['name'];
            let content=response.data['content'];
            display.innerHTML+='<div>\n' +
                '                   <span>作者id'+user_id+':name='+name+'</span>\n' +
                '               </div>';
            while(content.length>100){
                let temp=content.substring(0,100);
                content=content.substring(100);
                display.innerHTML+='<p>'+temp+'</p>\n';
            }
            display.innerHTML+='<p>'+content+'</p>\n';
            display.innerHTML+='<input type="button" value="删除" onclick="deleteArticle('+id+')">'
        })
    }else if(type==='forum'){
        axios({
            method:"get",
            url:'/api/admin/search/forum',
            params:param,
            headers:{
                Authorization: 'Bearer '+sessionStorage.getItem('token')
            }
        }).then(function (response){
            let user_id=response.data['user_id'];
            let name=response.data['name'];
            let content=response.data['content'];
            display.innerHTML+='<div>\n' +
                '                   <span>创建者id'+user_id+':name='+name+'</span>\n' +
                '               </div>';
            while(content.length>100){
                let temp=content.substring(0,100);
                content=content.substring(100);
                display.innerHTML+='<p>'+temp+'</p>\n';
            }
            display.innerHTML+='<p>'+content+'</p>\n';
            display.innerHTML+='<input type="button" value="删除" onclick="deleteForum('+id+')">'
        })
    }else if(type==='evaluate'){
        axios({
            method:"get",
            url:'/api/admin/search/evaluate',
            params:param,
            headers:{
                Authorization: 'Bearer '+sessionStorage.getItem('token')
            }
        }).then(function (response){
            let user_id=response.data['user_id'];
            let name=response.data['name'];
            let content=response.data['content'];
            display.innerHTML+='<div>\n' +
                '                   <span>发送者id'+user_id+':name='+name+'</span>\n' +
                '               </div>';
            while(content.length>100){
                let temp=content.substring(0,100);
                content=content.substring(100);
                display.innerHTML+='<p>'+temp+'</p>\n';
            }
            display.innerHTML+='<p>'+content+'</p>\n';
            display.innerHTML+='<input type="button" value="删除" onclick="deleteEvaluate('+id+')">'
        })
    }
}

function banUser(id){
    let param = new URLSearchParams();
    param.append('user_id',id);
    param.append('status','1');
    axios({
        method: 'PATCH',
        url:'/api/admin/users',
        params:param,
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        let status=response.data['status'];
        document.getElementById('banning').innerHTML='封禁';
    })
}
function unbanUser(id){
    let param = new URLSearchParams();
    param.append('user_id',id);
    param.append('status','0');
    axios({
        method: 'PATCH',
        url:'/api/admin/users',
        params:param,
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        let status=response.data['status'];
        document.getElementById('banning').innerHTML='解封';
    })
}

function closeSession(id){
    axios({
        method:"POST",
        url:'/api/admin/sessions/'+id,
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        alert(response.data['message']);
        document.getElementById('display').innerHTML='';
    })
}
function deleteArticle(id){
    axios({
        method: 'DELETE',
        url:'/api/articles/'+id,
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        alert(response.data['message']);
        document.getElementById('display').innerHTML='';
    })
}

function deleteForum(id){
    axios({
        method: 'DELETE',
        url:'/api/forums/'+id,
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        alert(response.data['message']);
        document.getElementById('display').innerHTML='';
    })
}
function deleteEvaluate(id){
    axios({
        method: 'DELETE',
        url:'/api/comments/'+id,
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        alert(response.data['message']);
        document.getElementById('display').innerHTML='';
    })
}
