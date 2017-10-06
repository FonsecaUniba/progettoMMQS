var el_log = null;
var items_row = 0;
var items_col = -1;
var items_res = '';
var selected_idx = 0;
var have_changes = false;
var lmb_pressed = false;
var level_data = [];
var mark_data = [];
var tmp_list = [];
var undo_buffer = [];
var undo_pos = 0;
var sel = { fx: 0, fy: 0, tx: 63, ty: 63 };
var mode = '';

function get(id)
{
	return document.getElementById(id);
}

function st(id)
{
	return document.getElementById(id).style;
}

function log(message)
{
	el_log.innerHTML += message + "\n";
	el_log.scrollTop = el_log.scrollHeight;
}

function get_cells_html()
{
	var res = '';

	for (var i = 0; i < 64; i++)
	{
		var line = [];
		var marks = [];

		for (var j = 0; j < 64; j++)
		{
			line.push(0);
			marks.push(0);

			res += '<div id="cell_' + j + '_' + i +
				'" _x="' + j + '" _y="' + i +
				'" onmousedown="return cell_clicked(this,event)" onmousemove="return cell_mousemove(this,event)" class="cell" style="top:' +
				(i * 64) + 'px;left:' +
				(j * 64) + 'px"></div>';
		}

		level_data.push(line);
		mark_data.push(marks);
	}

	return res;
}

function append_item(name, idx)
{
	items_col += 1;

	if (items_col >= 8)
	{
		items_col = 0;
		items_row += 1;
	}

	items_res += '<div id="item_' + idx + '" _idx="' + idx + '" class="cell" style="top:' +
		(items_row * 64) + 'px;left:' + (items_col * 64) +
		'px;background-image:url(' + name +
		'.png)" onmousedown="return item_clicked(this,event)"></div>';
}

function get_items_html_part()
{
    append_item('graphics/set-' + conf.set + '/floor/' + conf.floor, 0);

    	for (var i = 1; i <= 4; i++) {
    		append_item('graphics/common-' + conf.set + '/hero/hero_a' + i, i);
    	}

    	append_item('graphics/common-' + conf.set + '/trans/no-trans', 5);

    	items_col = -1;
    	items_row += 1;

    	for (var i = 1; i <= conf.walls; i++) {
    		var operand = '';

    		if(i < 10){
    			operand = '0';
    		} else {
    			operand = '';
    		}

    		append_item('graphics/set-' + conf.set + '/walls/wall_' + operand + i, i + 0x10 - 1);
    	}

    	items_col = -1;
    	items_row += 1;

    	for (var i = 1; i <= conf.trans_a; i++) {
    		var operand = '';

    		if(i < 10){
    			operand = '0';
    		} else {
    			operand = '';
    		}

    		append_item('graphics/set-' + conf.set + '/trans/trans_' + operand + i, i + 0x30 - 1);
    	}

    	items_col = -1;
    	items_row += 1;

    	for (var i = 1; i <= conf.trans_b; i++) {
    		var n = i + 8;

    		var operand = '';

    		if(n < 10){
    			operand = '0';
    		} else {
    			operand = '';
    		}


    		append_item('graphics/set-' + conf.set + '/trans/trans_' + operand + n, n + 0x30 - 1);
    	}

    	items_col = -1;
    	items_row += 1;

    	for (var i = 1; i <= conf.trans_c; i++) {
    		var n = i + 16;

    		var operand = '';

    		if(n < 10){
    			operand = '0';
    		} else {
    			operand = '';
    		}


    		append_item('graphics/set-' + conf.set + '/trans/trans_' + operand + n, n + 0x30 - 1);
    	}

    	return items_res;
}

function get_items_html()
{
    get('items').innerHTML = get_items_html_part();

	items_col = -1;
	items_row += 1;

	for (var i = 1; i <= conf.doors; i++) {
		append_item('graphics/set-' + conf.set + '/doors/door_' + i + '_f', i + 0x50 - 1);
	}

	items_col = -1;
	items_row += 1;

	for (var i = 1; i <= conf.objects; i++) {
		var operand = '';
		
		if(i < 10){
			operand = '0';
		} else {
			operand = '';
		}
		
		
		append_item('graphics/common-' + conf.set + '/objects/obj_' + operand + i, i + 0x70 - 1);
	}

	items_col = -1;
	items_row += 1;

	for (var i = 1; i <= conf.decor_a; i++) {
		var operand = '';
		
		if(i < 10){
			operand = '0';
		} else {
			operand = '';
		}
		
		
		append_item('graphics/set-' + conf.set + '/decor/decor_' + operand + i, i + 0x80 - 1);
	}

	items_col = -1;
	items_row += 1;

	for (var i = 1; i <= conf.decor_b; i++) {
		var n = i + 12;
		
		var operand = '';
		
		if(n < 10){
			operand = '0';
		} else {
			operand = '';
		}
		
		
		append_item('graphics/set-' + conf.set + '/decor/decor_' + operand + n, n + 0x80 - 1);
	}

	items_col = -1;
	items_row += 1;

	for (var i = 1; i <= conf.monsters; i++)
	{
		for (var j = 1; j <= 4; j++) {
			append_item('graphics/common-' + conf.set + '/monsters/mon_' + i + '_a' + j, 0xA0 + (i-1)*0x10 + j-1);
		}
	}

	return items_res;
}

function item_clicked(el)
{
	var idx = Number(el.getAttribute('_idx'));

	cancel_mode();

	get('item_' + selected_idx).className = 'cell';
	get('item_' + idx).className = 'cell cell-selected';
	selected_idx = idx;

	return false;
}

function getImage(idx)
{
	var el = document.getElementById('item_' + idx);
	
	if(el){
		return el.style.backgroundImage;
	} else {
		return '';
	}
}

function set_cell_value(el, x, y, idx)
{
	if (level_data[y][x] != idx)
	{
		if (!have_changes)
		{
			tmp_list = make_list();
			have_changes = true;
		}

		level_data[y][x] = idx;

		if (idx != 0) {
			el.style.backgroundImage = getImage(idx);
		} else {
			el.style.backgroundImage = '';
		}
	}
}

function cell_clicked(el)
{
	lmb_pressed = true;

	var x = Number(el.getAttribute('_x'));
	var y = Number(el.getAttribute('_y'));

	if (mode == '') {
		set_cell_value(el, x, y, selected_idx);
	} else {
		next_mode(x, y);
	}

	return false;
}

function cell_mousemove(el)
{
	if (lmb_pressed && mode=='') {
		cell_clicked(el);
	}
}

function try_undo()
{
	if (have_changes)
	{
		to_undo(tmp_list);
		have_changes = false;
	}
}

function global_onmouseup()
{
	lmb_pressed = false;
	try_undo();
	return false;
}

function is_empty_row(row)
{
	for (var i = 0; i < 64; i++) {
		if (level_data[row][i] != 0 || mark_data[row][i] != 0) {
			return false;
		}
	}

	return true;
}

function is_empty_col(col)
{
	for (var i = 0; i < 64; i++) {
		if (level_data[i][col] != 0 || mark_data[i][col]) {
			return false;
		}
	}

	return true;
}

function from_data()
{
	for (var i = 0; i < 64; i++)
	{
		for (var j = 0; j < 64; j++)
		{
			var el = get('cell_' + j + '_' + i);
			var el_st = el.style;

			if (level_data[i][j] != 0) {
				el_st.backgroundImage = getImage(level_data[i][j]);
			} else {
				el_st.backgroundImage = '';
			}

			if (mark_data[i][j] != 0) {
				el.innerHTML = mark_data[i][j];
			} else {
				el.innerHTML = '';
			}
		}
	}
}

function from_list(list)
{
	var pos = 0;

	for (var i = 0; i < 64; i++) {
		for (var j = 0; j <  64; j++) {
			level_data[i][j] = list[pos];
			pos++;
		}
	}

	for (var i = 0; i < 64; i++) {
		for (var j = 0; j <  64; j++) {
			mark_data[i][j] = list[pos];
			pos++;
		}
	}

	from_data();
}

function make_list()
{
	var list = [];

	for (var i = 0; i < 64; i++) {
		for (var j = 0; j < 64; j++) {
			list.push(level_data[i][j]);
		}
	}

	for (var i = 0; i < 64; i++) {
		for (var j = 0; j < 64; j++) {
			list.push(mark_data[i][j]);
		}
	}

	return list;
}

function to_undo(list)
{
	if (typeof(list) == 'undefined') {
		list = make_list();
	}

	if (undo_buffer.length > undo_pos + 1) {
		undo_buffer.splice(undo_pos);
	}

	if (undo_buffer.length > 64) {
		undo_buffer.splice(0, undo_buffer.length - 64);
	}

	undo_buffer.push(list);
	undo_pos = undo_buffer.length;
}

function do_undo()
{
	cancel_mode();

	if (undo_pos <= 0)
	{
		log("[E] Can't undo");
		return false;
	}

	if (undo_pos == undo_buffer.length)
	{
		to_undo();
		undo_pos--;
	}

	undo_pos--;
	from_list(undo_buffer[undo_pos]);

	return false;
}

function do_redo()
{
	cancel_mode();

	if ((undo_pos + 1) >= undo_buffer.length)
	{
		log("[E] Can't redo");
		return false;
	}

	undo_pos++;
	from_list(undo_buffer[undo_pos]);

	return false;
}

function move_up()
{
	cancel_mode();

	if (!is_empty_row(0))
	{
		log('[E] First row is not empty');
		return false;
	}

	to_undo();

	for (var i = 0; i < 63; i++)
	{
		for (var j = 0; j < 64; j++)
		{
			level_data[i][j] = level_data[i+1][j];
			mark_data[i][j] = mark_data[i+1][j];
		}
	}

	for (var i = 0; i < 64; i++)
	{
		level_data[63][i] = 0;
		mark_data[63][i] = 0;
	}

	from_data();
	return false;
}

function move_dn()
{
	cancel_mode();

	if (!is_empty_row(63))
	{
		log('[E] Last row is not empty');
		return false;
	}

	to_undo();

	for (var i = 63; i > 0; i--)
	{
		for (var j = 0; j < 64; j++)
		{
			level_data[i][j] = level_data[i-1][j];
			mark_data[i][j] = mark_data[i-1][j];
		}
	}

	for (var i = 0; i < 64; i++)
	{
		level_data[0][i] = 0;
		mark_data[0][i] = 0;
	}

	from_data();
	return false;
}

function move_lt()
{
	cancel_mode();

	if (!is_empty_col(0))
	{
		log('[E] First column is not empty');
		return false;
	}

	to_undo();

	for (var i = 0; i < 64; i++)
	{
		for (var j = 0; j < 63; j++)
		{
			level_data[i][j] = level_data[i][j+1];
			mark_data[i][j] = mark_data[i][j+1];
		}
	}

	for (var i = 0; i < 64; i++)
	{
		level_data[i][63] = 0;
		mark_data[i][63] = 0;
	}

	from_data();
	return false;
}

function move_rt()
{
	cancel_mode();

	if (!is_empty_col(63))
	{
		log('[E] Last column is not empty');
		return false;
	}

	to_undo();

	for (var i = 0; i < 64; i++)
	{
		for (var j = 63; j > 0; j--)
		{
			level_data[i][j] = level_data[i][j-1];
			mark_data[i][j] = mark_data[i][j-1];
		}
	}

	for (var i = 0; i < 64; i++)
	{
		level_data[i][0] = 0;
		mark_data[i][0] = 0;
	}

	from_data();
	return false;
}

function set_sel_class(className)
{
	for (var i = sel.fx; i <= sel.tx; i++) {
		for (var j = sel.fy; j <= sel.ty; j++) {
			get('cell_' + i + '_' + j).className = className;
		}
	}
}

function next_mode(x, y)
{
	if (mode == '') {
		return;
	}

	if (mode == 'select_from')
	{
		set_sel_class('cell');
		sel = { fx: x, fy: y, tx: x, ty: y };
		get('cell_' + sel.fx + '_' + sel.fy).className = 'cell cell-selected';
		mode = 'select_to';
		log('[R] Mark "to" point');
	}
	else if (mode == 'select_to')
	{
		sel = { fx: Math.min(sel.fx, x), fy: Math.min(sel.fy, y), tx: Math.max(sel.fx, x), ty: Math.max(sel.fy, y) };
		
		if(sel.fx==0 && sel.fy==0 && sel.tx==63 && sel.ty==63){
			set_sel_class('cell');
		} else {
			set_sel_class('cell cell-selected');
		}
		
		mode = '';
		log('[I] Area selected (w=' + (sel.tx-sel.fx+1) + ', h=' + (sel.ty-sel.fy+1) + ')');
	}
	else if (mode == 'mark_copy')
	{
		copy_or_move(x, y, false);
	}
	else if (mode == 'mark_move')
	{
		copy_or_move(x, y, true);
	}
	else if (mode == 'mark_mark')
	{
		var mark_val = Math.floor(Number(get('mark').value));
		mark_data[y][x] = mark_val;

		if (mark_val > 0 && mark_val < 255)		// from 1 to 254
		{
			get('cell_' + x + '_' + y).innerHTML = mark_val;
			get('mark').value = mark_val;
		}
		else
		{
			get('cell_' + x + '_' + y).innerHTML = '';
			get('mark').value = '';
		}
	}

	if (mode == '') {
		lmb_pressed = false;
	}
}

function cancel_mode()
{
	if (mode == '') {
		return;
	}

	if (mode == 'select_to')
	{
		get('cell_' + sel.fx + '_' + sel.fy).className = 'cell';
		sel = { fx: 0, fy: 0, tx: 63, ty: 63 };
	}

	mode = '';
	log('[I] Mode cancelled');
}

function select_all()
{
	cancel_mode();

	sel = { fx: 0, fy: 0, tx: 63, ty: 63 };
	set_sel_class('cell');

	log('[I] Entire level selected');
	return false;
}

function do_select()
{
	cancel_mode();
	mode = 'select_from';
	log('[R] Mark "from" point');
	return false;
}

function do_fill()
{
	for (var i = sel.fx; i <= sel.tx; i++) {
		for (var j = sel.fy; j <= sel.ty; j++) {
			set_cell_value(get('cell_' + i + '_' + j), i, j, selected_idx);
		}
	}

	try_undo();
	log('[I] Area filled');

	return false;
}

function hex(num)
{
	return Number(Math.floor(num / 0x10)).toString(16).toUpperCase() + Number(num % 0x10).toString(16).toUpperCase();
}

function do_save()
{
	var fx, fy, tx, ty;

	cancel_mode();

	for (fx = 0; fx < 64; fx++) {
		if (!is_empty_col(fx)) {
			break;
		}
	}

	for (tx = 63; tx >= 0; tx--) {
		if (!is_empty_col(tx)) {
			break;
		}
	}

	for (fy = 0; fy < 64; fy++) {
		if (!is_empty_row(fy)) {
			break;
		}
	}

	for (ty = 63; ty >= 0; ty--) {
		if (!is_empty_row(ty)) {
			break;
		}
	}

	if (fx > tx || fy > ty)
	{
		log('[E] Nothing to save');
		return false;
	}

	if ((tx - fx < 2) || (ty - fy < 2))
	{
		log('[E] Level is too small');
		return false;
	}

	var res = hex(ty - fy + 1) + hex(tx - fx + 1) + hex(fy) + hex(fx) + "\n";

	for (var i = fy; i <= ty; i++)
	{
		for (var j = fx; j <= tx; j++) {
			res += hex(level_data[i][j]);
		}

		res += "\n";
	}

	for (var i = fy; i <= ty; i++)
	{
		for (var j = fx; j <= tx; j++) {
			res += hex(mark_data[i][j]);
		}

		res += "\n";
	}

	get('data-text').value = res;
	st('data-text').display = '';
	//get('data-text').focus();

	log('[I] Data saved');
	return false;
}

var str_data = '';
var str_pos = 0;

function get_hex()
{
	if (str_pos + 1 >= str_data.length) {
		return -1;
	}

	var ret = parseInt(str_data.substr(str_pos, 2), 16);
	str_pos += 2;

	return ret;
}

function do_load()
{
	cancel_mode();

	str_data = get('data-text').value.replace(/[^0-9A-Fa-f]/g, '');
	str_pos = 0;

	var h = get_hex();
	var w = get_hex();
	var y = get_hex();
	var x = get_hex();

	if (h<=0 || w<=0 || x<0 || y<0)
	{
		log('[E] Invalid data (size or position)');
		return false;
	}

	var new_data = [];
	var new_marks = [];

	for (var i = 0; i < 64; i++)
	{
		var line = [];
		var marks = [];

		for (var j = 0; j < 64; j++)
		{
			line.push(0);
			marks.push(0);
		}

		new_data.push(line);
		new_marks.push(marks);
	}

	for (var i = 0; i < h; i++)
	{
		for (var j = 0; j < w; j++)
		{
			var idx = get_hex();

			if (idx < 0)
			{
				log('[E] Invalid data (cell data)');
				return false;
			}

			if (!get('item_' + idx)) {
				idx = 0x10;
			}

			new_data[i + y][j + x] = idx;
		}
	}

	for (var i = 0; i < h; i++)
	{
		for (var j = 0; j < w; j++)
		{
			var idx = get_hex();

			if (idx < 0)
			{
				log('[E] Invalid data (mark data)');
				return false;
			}

			new_marks[i + y][j + x] = idx;
		}
	}

	to_undo();
	level_data = new_data;
	mark_data = new_marks;
	from_data();

	st('data-text').display = 'none';
	log('[I] Level loaded');
	return false;
}

function do_copy()
{
	cancel_mode();
	mode = 'mark_copy';
	log('[R] Select "copy to" point');
	return false;
}

function do_move()
{
	cancel_mode();
	mode = 'mark_move';
	log('[R] Select "move to" point');
	return false;
}

function do_mark()
{
	cancel_mode();
	mode = 'mark_mark';
	log('[R] Select "mark" point');
	return false;
}

function copy_or_move(x, y, is_move)
{
	if (((x + sel.tx - sel.fx) > 63) || ((y + sel.ty - sel.fy) > 63))
	{
		log('[E] Area is too big for copying in selected place');
		log('[R] Select another point');
		return;
	}

	to_undo();
	var data = [];
	var marks = [];

	for (var i = sel.fy; i <= sel.ty; i++)
	{
		var line = [];
		var line_marks = [];

		for (var j = sel.fx; j <= sel.tx; j++)
		{
			line.push(level_data[i][j]);
			line_marks.push(mark_data[i][j]);
		}

		data.push(line);
		marks.push(line_marks);
	}

	if (is_move)
	{
		for (var i = sel.fy; i <= sel.ty; i++)
		{
			for (var j = sel.fx; j <= sel.tx; j++)
			{
				level_data[i][j] = 0;
				mark_data[i][j] = 0;
			}
		}
	}

	for (var i = 0; i <= sel.ty - sel.fy; i++)
	{
		for (var j = 0; j <= sel.tx - sel.fx; j++)
		{
			level_data[y + i][x + j] = data[i][j];
			mark_data[y + i][x + j] = marks[i][j];
		}
	}

	from_data();
	mode = '';

	if (is_move)
	{
		sel = { fx: 0, fy: 0, tx: 63, ty: 63 };
		set_sel_class('cell');

		log('[I] Area moved to new place');
	}
	else
	{
		log('[I] Area copied to new place');
	}
}

function clear_log()
{
	cancel_mode();
	el_log.innerHTML = '';
	return false;
}

function toggle_data_text()
{
	if(st('data-text').display == ''){
		st('data-text').display = 'none';
	} else {
		st('data-text').display = '';
	}
}

function do_convert()
{
	cancel_mode();

	str_data = get('data-text').value.replace(/[^0-9A-Fa-f]/g, '');
	str_pos = 0;

	var h = get_hex();
	var w = get_hex();
	var y = get_hex();
	var x = get_hex();

	if (h<=0 || w<=0 || x<0 || y<0)
	{
		log('[E] Invalid data (size or position)');
		return false;
	}

	var res = hex(h) + hex(w) + hex(y) + hex(x) + "\n";

	for (var i = 0; i < h; i++)
	{
		for (var j = 0; j < w; j++)
		{
			var idx = get_hex();

			if (idx < 0)
			{
				log('[E] Invalid data (cell data)');
				return false;
			}

			res += hex(idx);
		}

		res += "\n";
	}

	for (var i = 0; i < h; i++)
	{
		for (var j = 0; j < w; j++) {
			res += '00';
		}

		res += "\n";
	}

	get('data-text').value = res;
	st('data-text').display = '';
	get('data-text').focus();

	log('[I] Data converted');
	return false;
}

function main()
{
	el_log = get('log');

	st('level').backgroundImage = 'url(graphics/set-' + conf.set + '/floor/' + conf.floor + '.png)';
	get('level').innerHTML = get_cells_html();
	get('items').innerHTML = get_items_html();

	item_clicked(get('item_0'));
	document.onmouseup = global_onmouseup;

	log('[I] Initialized');
}
