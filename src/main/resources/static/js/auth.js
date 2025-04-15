function handleLogin(account,password){
    console.log(account);
    console.log(password);
    let param=new URLSearchParams();
    param.append('id',account);
    param.append('password',password);
    axios({
        method: 'POST',
        data: param,
        url: '/api/admin/login',
        type: JSON
    }).then(function (response){
        if(response.status === 200){
            sessionStorage.setItem('token', response.data.token);
            //console.log(localStorage.getItem('token'));
            location.href="/html/report.html"
        }else if(response.status === 400){
            alert("账户或密码错误");
        }
    }).catch(function (error) {
        if(error.response.status === 400){
            alert("账户或密码错误");
        }
    })
}