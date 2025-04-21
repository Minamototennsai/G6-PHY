function checkToken(token){
    let param = new URLSearchParams();
    param.append('token',token);
    axios({
        method: 'GET',
        url: '/api/token',
        type:JSON,
        params:param
    }).then(function (response){
        if(!response.data['isCorrect']){
            alert('你必须先登录');
            sessionStorage.setItem('token', null);
            location.href="../index.html";
        }
    }).catch(function (error) {
        alert('你必须先登录');
        sessionStorage.setItem('token', null);
        location.href="../index.html";
    })
}