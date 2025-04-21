function daily(){
    axios({
        method: 'get',
        url:'/api/admin/daily',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        document.getElementById('onlineNum').innerHTML=response.data['num'];
    })
}
function scoreRank(){
    axios({
        method: 'get',
        url: '/api/scorerank',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        let consultants=response.data['consultants'];
        let object=document.getElementById('scoreList');
        for(let i in consultants){
            object.innerHTML+='<br>' +
                              '<span>'+consultants[i]['id']+':'+consultants[i]['name']+'...score:'+consultants[i]['score']+'</span>'
        }
    })
}
function sessionRank(){
    axios({
        method: 'get',
        url: '/api/sessionrank',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        let consultants=response.data['consultants'];
        let object=document.getElementById('sessionList');
        for(let i in consultants){
            object.innerHTML+='<br>' +
                '<span>'+consultants[i]['id']+':'+consultants[i]['name']+'...score:'+consultants[i]['num_session']+'</span>'
        }
    })
}
function nowSession(){
    axios({
        method:'get',
        url:'/api/nowsession',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        let total=response.data['total'];
        let session=response.data['sessions'];
        document.getElementById('nowSession').innerHTML+='<br>' +
            '<span>一共'+total+'条记录</span>';
        for(let i in session){
            document.getElementById('nowSession').innerHTML+='<br>' +
                '<span>会话id:'+session[i]['id']+'</span>';
        }
    })
}

function getDateSchedule(date){
    axios({
        method: 'get',
        url:'/api/schedule/week',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem('token')
        }
    }).then(function (response){
        let panel=document.getElementById('panel');
        panel.innerHTML='';
        let schedule=response.data['schedules'];
        for(let i in schedule){
            if(schedule[i]['time']!==date){
                continue;
            }
            panel.innerHTML+='<br>' +
                '<span>咨询者'+schedule[i]['fromid']+schedule[i]['fromname']+'预约咨询师'+schedule[i]['toid']+schedule[i]['toname']+'</span>'
        }
    })
}