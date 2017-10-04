<?php

$mode = (isset($_REQUEST['mode']) ? $_POST['mode'] : '');

switch (strtolower($mode))
{
	case 'load_map':
		$name = preg_replace('/^[a-z0-9\-_.]/i', '', $_POST['name']);

        $to_print = htmlspecialchars( @file_get_contents("levels/$name") );
        echo $to_print;

		//echo @file_get_contents("levels/$name");
		break;

	case 'save_map':
		$name = preg_replace('/^[a-z0-9\-_.]/i', '', $_POST['name']);
		$data = $_POST['map'];

		$res = @file_put_contents("levels/$name", $data);

        $to_print = $res ? "File [$name] saved" : "Cant save file [$name]"
        echo htmlspecialchars( $to_print );

		//echo $res ? "File [$name] saved" : "Cant save file [$name]";
		break;

	default:
		$handle = opendir('levels');
		$res = array();

		if ($handle) {
			while (false !== ($file = readdir($handle))) {
				if (!is_dir("levels/$file") && strpos($file, "actions") !== 0) {
					$res[] = $file;
				}
			}
		}

		natsort($res);
		$res = array_values($res);

		$to_print = htmlspecialchars( json_encode($res) );
		echo $to_print;

		//echo json_encode($res);
		break;
}
