document.addEventListener('DOMContentLoaded', function () {
    const signupForm = document.querySelector('section');
    signupForm.style.opacity = 0;

    setTimeout(() => {
        signupForm.style.transition = 'opacity 1s ease-in-out';
        signupForm.style.opacity = 1;
    }, 500);

    const signupButton = document.querySelector('button');

    signupButton.addEventListener('click', function (event) {
        event.preventDefault(); // Prevent form submission

        const emailInput = document.querySelector('input[type="email"]');
        const passwordInput = document.querySelector('input[name="password"]');
        const confirmPasswordInput = document.qu
        erySelector('input[name="passwordcon"]');

        // Check for valid inputs
        const isValid = emailInput.checkValidity() && passwordInput.checkValidity() && confirmPasswordInput.checkValidity();

        if (!isValid) {
            signupForm.classList.add('shake');

            setTimeout(() => {
                signupForm.classList.remove('shake');
            }, 1000);

            return; // Stop further execution
        }

        // Check if passwords match
        if (passwordInput.value !== confirmPasswordInput.value) {
            alert('Passwords do not match!');
            return;
        }

        // Proceed with form submission or other logic
        alert('Form is valid and ready for submission!');
    });
});
