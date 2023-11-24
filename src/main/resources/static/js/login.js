// 로그인 로직
function login(event) {
  event.preventDefault(); // 폼의 기본 제출 동작을 방지

  const email = document.querySelector('input[name="email"]').value;
  const password = document.querySelector('input[name="password"]').value;

  axios.post('/api/login', {
    email: email,
    password: password
  }, {
    headers: {
      'Content-Type': 'application/json'
    }
  })
          .then(function (response) {
            console.log('Logged in successfully', response);
          })
          .catch(function (error) {
            console.error('Login failed', error);
        
            // 에러 스타일 추가
            applyErrorStyle(loginForm);
        
            // 에러 메시지 설정
            let errorMessage = '이메일 및 비밀번호 조합이 잘못되었습니다.';
            if (error.response && error.response.data && error.response.data.message) {
                errorMessage = error.response.data.message;
            }
            document.getElementById('error_msg').innerText = errorMessage;
        
            // 일회성으로 input 이벤트 발생 시 에러 스타일 및 메시지 초기화
            loginForm.addEventListener('input', function(event) {
                resetErrorStyle(loginForm);
                document.getElementById('error_msg').innerText = '';
        
                // 이벤트 리스너 제거는 자동으로 수행됨
            }, { once: true });
        })
}


const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', e => login(e));

// 눈 깜빡깜박
  $(document).ready(function(){
    $('.password_input i').on('click',function(){
        $('input').toggleClass('active');
        if($('input').hasClass('active')){
            $(this).attr('class',"fa-regular fa-eye-slash")
            .prev('input').attr('type',"text");
        }else{
            $(this).attr('class',"fa-regular fa-eye")
            .prev('input').attr('type','password');
        }
    });
});

// 페이지 로드 시 뒤로가기 버튼 추가
document.addEventListener("DOMContentLoaded", function() {
  const prevBtn = document.getElementById('prev_btn');
  if (prevBtn) {
    prevBtn.addEventListener('click', function() {
      history.go(-1);
    });
  }
});

// 비밀번호 틀렸을 때 실행되는 너구리
function applyErrorStyle(formElement) {
  const inputList = formElement.querySelectorAll('input');
  inputList.forEach((input) => {
      input.style.color = 'rgb(188, 41, 56)';
      input.style.backgroundColor = 'rgb(217, 216, 217)';
  });
}

function resetErrorStyle(formElement) {
  const inputList = formElement.querySelectorAll('input');
  inputList.forEach((input) => {
      input.style.color = ''; // 기본값으로 재설정
      input.style.backgroundColor = ''; // 기본값으로 재설정
  });
}