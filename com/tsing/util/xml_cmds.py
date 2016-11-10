set_xml = '''
<?xml version="1.0" encoding="UTF-8"?>
<msg>
<pwd>ff2234asdxcn2s34</pwd>
<cmd>set</cmd>
<mac>%s</mac>
<mac_crc>%s</mac_crc>
<sn>%s</sn>
<sn_crc>%s</sn_crc>
<sender>pc</sender>
</msg>
'''

get_xml = '''
<?xml version="1.0" encoding="UTF-8"?>
<msg>
<pwd>ff2234asdxcn2s34</pwd>
<cmd>get</cmd>
<list>sn,mac </list>
<sender>pc</sender>
</msg>
'''

erase_xml = '''
<?xml version="1.0" encoding="UTF-8"?>
<msg>
<pwd>ff2234asdxcn2s34</pwd>
<cmd>set</cmd>
<mac></mac>
<sn></sn>
</msg>
'''

reboot_xml = '''
<?xml version="1.0" encoding="UTF-8"?>
<msg>
<pwd>ff2234asdxcn2s34</pwd>
<cmd>set</cmd>
<stb>reboot</stb>
</msg>
'''
