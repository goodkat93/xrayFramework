token=$(curl -H "Content-Type: application/json" -X POST --data @"xray_cloud_auth.json" https://xray.cloud.getxray.app/api/v2/authenticate | tr -d '"')
curl -H "Content-Type: application/json" -X POST -H "Authorization: Bearer $token" --data @"target/surefire-reports/xray-report.json" https://xray.cloud.getxray.app/api/v1/import/execution