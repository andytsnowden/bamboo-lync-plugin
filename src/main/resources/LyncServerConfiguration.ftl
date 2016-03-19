<html>
<head>
[@ui.header pageKey="Lync Server Global Configuration" title=true /]
    <meta name="decorator" content="adminpage">
</head>
<body>

[@ui.clear /]

    [@ww.form
id='LyncServerConfiguration'
action='updateConfigureLyncServer.action'
submitLabelKey='Update Configuration'
titleKey='Lync Server'
description='Send IM notifications using Microsoft Lync 2013.']

    [@ww.textfield name='lyncServer' labelKey="lync.server" required='true' value=lync.server descriptionKey='lync.server.description' /]

    [@ui.clear /]

[/@ww.form]


</body>
</html>