const errorMessages = {
    permissions: window.modalDataErrorText.permissions,
    configDisabled: window.modalDataErrorText.disabled
}

angular.module('pocketModal', [])
    .service('PocketService', ['$http', function ($http) {
        this.getPockets = () => {
            return $http({
                method: 'GET',
                url: '/api/configuration/members-pockets?guildId='+  window.guildId,
                headers: 'Accept:application/json'
            }).then((res) => {
                return res.data
            })
        }

        this.sendTransaction = (guildId, memberId, description, diff, frozen, pocketId) => {
            return $http({
                method: 'POST',
                url: '/api/configuration/save-pocket',
                data: {
                    [window.csrf.parameterName]: window.csrf.token,
                    guildId: guildId,
                    memberId,
                    description,
                    diff,
                    frozen,
                    pocketId
                },
                headers: {
                    [window.csrf.headerName]: window.csrf.token
                }
            })
        }
    }])
    .controller('PocketController', ['$scope', 'PocketService', function ($scope, PocketService) {
        $scope.showError = false
        $scope.showLoading = false
        $scope.errorMessage = errorMessages.configDisabled
        $scope.pocketsData = []

        $('#pocketModal')
            .on('shown.bs.modal', function () {
                $scope.getPockets()
            })
            .on('hidden.bs.modal', function () {
                $scope.showError = false
                $scope.showLoading = false
                $scope.errorMessage = ''
                $scope.pocketsData = []
            })
        $scope.sendUpdate = (pocket, index) => {
            $scope.showLoading = true;
            $scope.showError = false;
            PocketService.sendTransaction(window.guildId, pocket.userId, pocket.description, pocket.diff, pocket.frozen, pocket.pocketId)
                .then((suc) => {
                    $scope.showLoading = false;
                    $scope.pocketsData[index] = suc.data
                    console.log(suc.data)
                }).catch((e) => {
                $scope.showLoading = false
                $scope.showError = true
                $scope.errorMessage = `${e.data.errorMessage}. Error code: ${e.data.errorCode}`
            })
        }

        $scope.getPockets = () => {
            $scope.showLoading = true
            PocketService.getPockets()
                .then((data) => {
                    $scope.showError = false
                    $scope.showLoading = false
                    $scope.pocketsData = data
                }).catch(err => {
                    $scope.showLoading = false
                    $scope.showError = true
                    $scope.errorMessage = errorMessages[err.data.errorMessage]
            })
        }
}])
