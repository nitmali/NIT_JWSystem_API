const resultApp = new Vue({
    el: '#result',
    data: {
    },
        userId: '',
        password: '',
        key: '',
    methods: {
        getResult:function () {
            $.get("127.0.0.1:8023/getResultDirect",
                {
                    userId: resultApp.userId,
                    password: resultApp.password,
                    key: resultApp.key
                },
                function (message) {
                    alert(message);
                }
            );
        }
    }
});