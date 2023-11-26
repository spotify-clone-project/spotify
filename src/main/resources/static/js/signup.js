const step = 'step';
let current = 1;

function next() {
    changeStep(1);
}

function prev() {
    changeStep(-1);
}

function changeStep(stepChange) {
    const oldStep = "step" + current;
    const oldSection = document.getElementById(oldStep);

    current += stepChange;
    const newStep = "step" + current;
    const newSection = document.getElementById(newStep);


    if (newSection) {
        newSection.classList.add('current');
    }
    if (oldSection) {
        oldSection.classList.remove('current');
    }
}


function updateCurrentStep(isAdding) {
    const currentStep = "step" + current; 
    const section = document.getElementById(currentStep);
    if (section) {
        if (isAdding) {
            section.classList.add('current');
        } else {
            section.classList.remove('current');
        }
    }
}

// 초기 상태 설정
updateCurrentStep(true);

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


// 버튼 이벤트 리스너 추가
const prevBtnList = document.querySelectorAll('.prev_btn');
prevBtnList.forEach((e) => {
    e.addEventListener('click', prev); // 함수 참조
});

const nextBtnList = document.querySelectorAll('.next_btn, .join_btn');
nextBtnList.forEach((e) => { // 여기는 nextBtnList가 되어야 합니다.
    e.addEventListener('click', next); // 함수 참조
});

// 회원가입 로직
function signup(event) {
    const email = document.querySelector('input[name="email"]').value;
    const password = document.querySelector('input[name="password"]').value;
    const birth = document.querySelector('input[name="birth"]').value;
    const gender = document.getElementById('gender').value;
    const name = document.querySelector('input[name="name"]').value;
  
    axios.post('/api/auth/signup', {
      email: email,
      password: password,
      birth: birth,
      gender: gender,
      name: name,
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(function (response) {
        console.log('Signup in successfully', response);
    })
    .catch(function (error) {
        console.error('Signup failed', error);
    })
  }
  
  

const signupBtn = document.getElementById('signup_btn');
signupBtn.addEventListener('click', e => signup(e))