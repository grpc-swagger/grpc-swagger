<!DOCTYPE html>
<html lang="en">
<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta http-equiv="x-ua-compatible" content="ie=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

  <title>gRPC-Swagger</title>

  <link rel="stylesheet" href="static/lib/mobi.min.css"/>
  <link rel="stylesheet" href="static/lib/mobi-plugin-color.min.css"/>
  <link rel="stylesheet" href="static/lib/mobi-plugin-form.min.css"/>
  <link rel="stylesheet" href="static/lib/mobi-plugin-flexbox.min.css"/>

  <style type="text/css">
    h2 {
      border-bottom: 1px solid #EFEAEA;
      padding-bottom: 10px;
    }

    input[type="text"] {
      font-size: 1.5em;
      height: 2.5em;
    }

    #services-display {
      width: 100%;
      max-height: 500px;
    }

    td {
      font-size: 1.3em;
      line-height: 1.3em;
    }

    #container table {
      margin: 0.1em 0;
    }

    .hide {
      visibility: hidden;
    }

  </style>
</head>
<body>
<div class="flex-center">
  <div class="container" id="container">
    <article class="site-article unit-1-on-mobile">
      <h2>Endpoint Register</h2>
      <span>${endpoint}</span>

      <h2>Services</h2>
      <div class="flex-left units-gap" id="services-display-wrapper">
        <ul id="services-display">
        </ul>
      </div>
    </article>
  </div>
</div>

<script type="text/javascript" src="static/lib/jquery.min.js"></script>
<script type="text/javascript" src="static/lib/jsoneditor.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        String.prototype.format = function () {
            let a = this
            for (let k in arguments) {
                a = a.replace("{" + k + "}", arguments[k])
            }
            return a
        }
        const linkPattern = "<li><a href='ui/index.html?url={0}' target='_blank'>{1}</a></li>"
        const apiUrl = "{0}/v2/api-docs?service={1}"
        let serverBaseUrl = "${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}"

        let texts = "${endpoint}".split(":")
        let data = "host=" + texts[0] + "&port=" + texts[1]

        $.ajax({
            url: serverBaseUrl + "/register",
            type: "POST",
            data: data,
            success: function (e) {
                let services = e.data
                services.forEach(function (element) {
                    let url = encodeURIComponent(apiUrl.format(serverBaseUrl, element['service']));
                    $("#services-display").append(linkPattern.format(url, element['service']));
                })
            },
            error: function (e) {
                console.error(e)
                alert("Register failed!")
            }
        })
    });
</script>
</body>
</html>