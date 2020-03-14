/*
 * Copyright (C) 2020 Michael Male
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

insert into points_of_interest(id, name, description, latitude, longitude) values (1, 'Dyfi Wildlife Centre',
                                                                                   'Place One Description',
                                                                                   52.568774, -3.918031) on
    conflict do nothing;
insert into points_of_interest(id, name, description, latitude, longitude) values (2, 'Aberystwyth University',
                                                                                   'Place Two Description',
                                                                                   52.4125057, -4.06108) on conflict
do nothing;

insert into points_of_interest(id, name, description, latitude, longitude) values (3, 'Glyndale', 'Place Three ' ||
                                                                                                  'Description',
                                                                                   52.4162989, -4.07767) on conflict
do nothing;

insert into points_of_interest(id, name, description, latitude, longitude) values (4, 'Poole Harbour', 'Place Four ' ||
                                                                                                       'Description',
                                                                                   50.6937948, -2.0061704) on
                                                                                       conflict do nothing;